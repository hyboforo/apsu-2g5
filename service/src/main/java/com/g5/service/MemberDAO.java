/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.g5.service;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateJdbcException;

/**
 *
 * @author GEN-NTB-431
 */
public class MemberDAO extends DefaultHibernateDAOImpl{
    
    private static final Logger logger = LoggerFactory.getLogger(MemberDAO.class);
    
     public List<Members> getAllMembers(){
         Criteria cr = createCriteria(Members.class);      
        List<Members> members = cr.list();
        logger.info(members.size() + " members found");
        return members;
            
    }
     
      public void saveOrUpdate(Members members){
        try {
           super.saveOrUpdate(members);
        } catch (HibernateJdbcException ex) {
            logger.error(ex.getLocalizedMessage());
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }
    }
    
    /**
     * Write a new offender into the database 
     * @param members
     */
    public void persistMember(Members members){
        try {
           super.persist(members);
        } catch (HibernateJdbcException ex) {
            logger.error(ex.getLocalizedMessage());
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }
        
    }
    
    public List<Members> getMemberByHouse(String house){
        Criteria cr = createCriteria(Members.class);
        cr.add(Restrictions.eq("house", house));
        List<Members> members =  cr.list();
        return members;
        
    }
    
     public List<Members> getMemberByFristName(String firstName){
        Criteria cr = createCriteria(Members.class);
        cr.add(Restrictions.eq("firstName", firstName));
        List<Members> members =  cr.list();
        return members;
        
    }
     
     public List<Members> getMemberByLastName(String lastName){
        Criteria cr = createCriteria(Members.class);
        cr.add(Restrictions.eq("lastName", lastName));
        List<Members> members =  cr.list();
        return members;
        
    }
    
     public List<Members> getMemberByCourse(String course){
        Criteria cr = createCriteria(Members.class);
        cr.add(Restrictions.eq("program", course));
        List<Members> members =  cr.list();
        return members;
        
    }
     
     public List<Members> getMemberByPhone(String phone){
        Criteria cr = createCriteria(Members.class);
        cr.add(Restrictions.eq("primaryNumber", phone));
        List<Members> members = cr.list();
        return members;
        
    }
     
     public List<Members> getMemberByClass(String courseClass){
        Criteria cr = createCriteria(Members.class);
        cr.add(Restrictions.eq("courseClass", courseClass));
        List<Members> members =  cr.list();
        return members;
        
    }
     
     public List<Members> getMemberByCountry(String country){
        Criteria cr = createCriteria(Members.class);
        cr.add(Restrictions.eq("country", country));
        List<Members> members =  cr.list();
        return members;
        
    }
     
     public List<Members> getMemberByCity(String city){
         Criteria cr = createCriteria(Members.class);
         cr.add(Restrictions.eq("city", city));
         List<Members> members = cr.list();
         return members;
     }
     
     public List<Members> getMemberByRegion(String region){
         Criteria cr = createCriteria(Members.class);
         cr.add(Restrictions.eq("region", region));
         List<Members> members = cr.list();
         return members;
     }
     
     public List<Members> getMemberByCOurseClass(String courseClass){
         Criteria cr = createCriteria(Members.class);
         cr.add(Restrictions.eq("courseClass", courseClass));
         List<Members> members = cr.list();
         return members;
     }
     
     public Members getMemberByID(int id){
         Criteria cr = createCriteria(Members.class);
         cr.add(Restrictions.eq("id", id));
         Members members = (Members) cr.uniqueResult();
         return members;
     }
    
}
