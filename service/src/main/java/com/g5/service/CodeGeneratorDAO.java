/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.g5.service;

import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.springframework.orm.hibernate3.HibernateJdbcException;

/**
 *
 * @author GEN-NTB-431
 */
public class CodeGeneratorDAO extends DefaultHibernateDAOImpl{
    
     private static final Logger logger = Logger.getLogger(CodeGeneratorDAO.class);
    
    
    public List<CodeGenerator> getAllCodes(){
         Criteria cr = createCriteria(CodeGenerator.class);      
        List<CodeGenerator> codes = cr.list();
        logger.info(codes.size() + " codes found");
        return codes;
            
    }
    
     
      public void saveOrUpdate(CodeGenerator codes){
        try {
           super.saveOrUpdate(codes);
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
    public void persistMember(CodeGenerator code){
        try {
           super.persist(code);
        } catch (HibernateJdbcException ex) {
            logger.error(ex.getLocalizedMessage());
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }
        
    }
    
    public List<CodeGenerator> getCodeByPhone(String phone){
        Criteria cr = createCriteria(CodeGenerator.class);
        cr.add(Restrictions.eq("phoneNumber", phone));
        List<CodeGenerator> code =  cr.list();
        return code;
        
    }
    
    public CodeGenerator getCodeByToken(String token){
        Criteria cr = createCriteria(CodeGenerator.class);
        cr.add(Restrictions.eq("token", token));
        CodeGenerator code = (CodeGenerator) cr.uniqueResult();
        return code;
        
    }
    
}
