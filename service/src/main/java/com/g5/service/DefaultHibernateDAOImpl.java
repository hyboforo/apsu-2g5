/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.g5.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.jdbc.support.lob.LobHandler;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 *
 * @author GEN-NTB-431
 */
public abstract class DefaultHibernateDAOImpl<E> implements BeanFactoryAware {

    private static final long serialVersionUID = 837056958164633603L;

    private Logger logger = LoggerFactory.getLogger(DefaultHibernateDAOImpl.class);
    protected BeanFactory beanFactory;
    private Class<E> persistentClass;
    private String persistentClassName;
    private SessionFactory sessionFactory;

    DataSource dataSource;

    LobHandler lobHandler;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public E getById(Serializable id) {
//        String stringId = (String) id;
        logger.debug("Getting entity instance by id");
        E e = (E) this.getSession().load(getPersistentClass(), id);
        return e;
    }

    /**
     * @see {@link DAO#getReferenceById(Object)}
     */
    public E getReferenceById(Serializable id) {
        String stringId = (String) id;
        E e = (E) this.getSession().get(getPersistentClass(), stringId);
        logger.debug("Entity referenced by id");
        return e;
    }

    public List<E> getBatch(int start, int interval) throws Exception {
        List<E> result = null;

        try {
            Criteria criteria = createCriteria(getPersistentClass());
            if (start == 0 && interval == 0) {
                result = criteria.list();
                logger.info(result.size() + " BiometricSubjects fetched");
                return result;
            } else if (start >= 0 && interval > 0) {
                result = criteria.setMaxResults(interval).setFirstResult(start).list();
                return result;
            } else {
                logger.error("Invalid interval supplied for query");
                return null;
            }
        } catch (HibernateException e) {
            logger.error("Unable to retrieve " + getPersistentClass() + ": " + e.getMessage());
            throw new Exception(e);
        }

    }

    public Long getCount() throws Exception {
        logger.debug("DAO called to get count ");
        Long result = null;

        try {
            logger.debug("Counting the number of " + getPersistentClass().getName());

            String query = "select count(id) from " + getPersistentClass().getName();
            result = (Long) createQuery(query).uniqueResult();

            logger.info("Done counting " + getPersistentClass().getName() + ":[ count: " + result + "]");
        } catch (HibernateException e) {
            logger.error("Unable to count " + getPersistentClass().getName() + " Error: " + e.getMessage());
            throw new Exception(e);
        }

        return result;
    }

//    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        this.beanFactory = beanFactory;
//    }
    public void init() {
        if (this.getPersistentClassName() == null || this.getPersistentClassName().equalsIgnoreCase("")) {
            logger.error("Fatal: No persistentClassName provided. DAO cannot be instantiated without it");
            throw new IllegalArgumentException("Fatal: No persistentClassName provided. DAO cannot be instantiated without it");
        }
        if (this.beanFactory == null) {
            logger.error("Fatal: No BeanFactory instance set. DAO cannot be instantiated without it");
            throw new IllegalArgumentException("Fatal: No BeanFactory instance set. DAO cannot be instantiated without it");
        }
        try {
            Class clazz = (Class) beanFactory.getType(getPersistentClassName());
            this.setPersistentClass(clazz);
            logger.info("Persistence class of " + this.getClass().getName() + " set to " + clazz.getName());
        } catch (BeansException beansException) {
            logger.error("Fatal: Exception thrown initializing DAO: " + beansException.getLocalizedMessage());
            throw new IllegalArgumentException("Fatal: Exception thrown initializing DAO: " + beansException.getLocalizedMessage());
        }

    }

    /* (non-Javadoc)
     * @see com.genkey.abis.components.dao.HibernateDAO#createQuery(java.lang.String)
     */
    public Query createQuery(String hql) {
        Query query = null;

        if (sessionFactory.getCurrentSession() != null) {
            query = sessionFactory.getCurrentSession().createQuery(hql);
            logger.debug("Hibernate query created");
        } else {
            logger.error("No Session exits in current thread to create query");
        }
        return query;

    }

    public Query createSQLQuery(String sql) {
        Query query = null;

        if (sessionFactory.getCurrentSession() != null) {
            query = sessionFactory.getCurrentSession().createSQLQuery(sql);
            logger.debug("SQL query created");
        } else {
            logger.error("No Session exits in current thread to create query");
        }
        return query;

    }

    protected Criteria criteria() {
        return createCriteria(this.getPersistentClass());
    }

    protected Criteria criteriaRange(String attribute, Object low, Object high) {
        return criteria().add(Restrictions.between(attribute, low, high));
    }

    protected Criteria criteria(String attribute, Object value) {
        return criteria().add(Restrictions.eq(attribute, value));
    }

    public boolean existsForCriteria(Criteria criteria) {
        List<Serializable> result = criteriaFetchIds(criteria);
        return result != null && result.size() > 0;
    }

    public List<Serializable> criteriaFetchIds(Criteria criteria) {
        criteria.setProjection(Projections.id());
        List<Serializable> result = criteria.list();
        return result;
    }

    /* (non-Javadoc)
     * @see com.genkey.abis.components.dao.HibernateDAO#createCriteria(Class)
     */
    public Criteria createCriteria(Class myClass) {
        Criteria criteria = null;

        if (sessionFactory.getCurrentSession() != null) {
            criteria = sessionFactory.getCurrentSession().createCriteria(myClass);
            logger.debug("Hibernate criteria created");
        }

        return criteria;

    }

    public Criteria createCriteria(Class myClass, String alias) {
        Criteria criteria = null;

        if (sessionFactory.getCurrentSession() != null) {
            criteria = sessionFactory.getCurrentSession().createCriteria(myClass, alias);
            logger.debug("Hibernate criteria created");
        }

        return criteria;

    }

    /* (non-Javadoc)
     * @see com.genkey.abis.components.dao.HibernateDAO#delete(E)
     */
    public void delete(E entity) throws Exception {
        if (sessionFactory.getCurrentSession() == null) {
            throw new Exception("Hibernate sessionFactory.getCurrentSession() not initialized");
        }
        try {
            sessionFactory.getCurrentSession().delete(entity);
            logger.info("sessionFactory.getCurrentSession() has removed entity. Waiting on commit");
        } catch (HibernateException e) {
            logger.error("HibernateException thrown trying to delete entity ->" + entity.toString());
            throw new Exception(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see com.genkey.abis.components.dao.HibernateDAO#getById(java.io.Serializable)
     */
//    public abstract E getById(Serializable id);
//
//    /* (non-Javadoc)
//     * @see com.genkey.abis.components.dao.HibernateDAO#getReferenceById(java.io.Serializable)
//     */
//    public abstract E getReferenceById(Serializable id);

    /* (non-Javadoc)
     * @see com.genkey.abis.components.dao.HibernateDAO#merge(E)
     */
    public E merge(E entity) throws Exception {
        if (sessionFactory.getCurrentSession() == null) {
            throw new Exception("Hibernate sessionFactory.getCurrentSession() not initialized");
        }
        E e = (E) sessionFactory.getCurrentSession().merge(entity);
        logger.debug("Entity merged into sessionFactory.getCurrentSession()");
        return e;
    }

    /* (non-Javadoc)
     * @see com.genkey.abis.components.dao.HibernateDAO#persist(java.lang.Object)
     */
    public void persist(Object object) throws Exception {
        if (sessionFactory.getCurrentSession() == null) {
            throw new Exception("Hibernate sessionFactory.getCurrentSession() not initialized");
        }
        try {
            if (object instanceof Collection) {
                logger.debug("Persisting collection of entities");
                Collection objects = (Collection) object;
                for (Object o : objects) {
                    sessionFactory.getCurrentSession().persist(o);
                }
                logger.debug("Collection of entities persisted");
            } else {
                sessionFactory.getCurrentSession().persist(object);
                logger.debug("Single entity persisted");
            }
        } catch (HibernateException e) {
            logger.warn(
                    "HibernateException thrown trying to persist entities: " + e.getLocalizedMessage());
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            logger.warn(
                    "Exception thrown trying to persist entities");
            throw new Exception(e.getMessage());
        }

    }

    /* (non-Javadoc)
     * @see com.genkey.abis.components.dao.HibernateDAO#saveOrUpdate(java.lang.Object)
     */
    public void saveOrUpdate(Object object) throws Exception {
        logger.debug("Save or update " + this.getName());
        if (sessionFactory.getCurrentSession() == null) {
            throw new Exception("Hibernate sessionFactory.getCurrentSession() not initialized");
        }
        try {
            if (object instanceof Collection) {
                logger.debug("Saving/Updating collection of entities");
                Collection objects = (Collection) object;
                for (Object o : objects) {
                    sessionFactory.getCurrentSession().saveOrUpdate(o);
                }
                logger.debug("Collection of entities saved/updated");
            } else {
                sessionFactory.getCurrentSession().saveOrUpdate(object);
                logger.debug("Single entity saved/updated");
            }
        } catch (HibernateException e) {
            logger.error(
                    "HibernateException thrown trying to save/update entities: " + e.getLocalizedMessage());
            throw new Exception(e);
        } catch (Exception e) {
            logger.warn(
                    "Exception thrown trying to save/update entities");
            throw new Exception(e);
        }

    }

    private String getName() {
        // TODO Auto-generated method stub
        return this.persistentClassName != null ? persistentClassName
                : (this.persistentClass != null ? this.persistentClass.getName()
                        : this.getClass().getName());
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public Class<E> getPersistentClass() {
        return persistentClass;
    }

    public void setPersistentClass(Class<E> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public String getPersistentClassName() {
        return persistentClassName;
    }

    public void setPersistentClassName(String persistentClassName) {
        this.persistentClassName = persistentClassName;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public LobHandler getLobHandler() {
        return lobHandler;
    }

    public void setLobHandler(LobHandler lobHandler) {
        this.lobHandler = lobHandler;
    }

}
