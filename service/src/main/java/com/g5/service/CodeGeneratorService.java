/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.g5.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author GEN-NTB-431
 */
public class CodeGeneratorService {

    CodeGeneratorDAO codeGeneratorDAO;

    public String generateCode() {
        UUID id = UUID.randomUUID();
        String stringID = id.toString();
        return stringID;
    }

    public String returnDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        return date;
    }

    public boolean compareCode(String code) {
        System.out.println("Comparing tokens");
        CodeGenerator codeByToken = codeGeneratorDAO.getCodeByToken(code);
        if (codeByToken == null) {
            System.out.println("Token not Found");
            return false;
        } else if (codeByToken.getStatus() == 1) {
            return false;
        }
        System.out.println("Token found");
        return true;
    }
    
    public List<CodeGenerator> getAllCodes(){
        List<CodeGenerator> allCodes = codeGeneratorDAO.getAllCodes();
        return allCodes;
    }

    public CodeGenerator getCode(String token) {
        System.out.println("Comparing tokens");
        CodeGenerator codeByToken = codeGeneratorDAO.getCodeByToken(token);
        return codeByToken;
    }
    
    public List<CodeGenerator> getCodeByPhone(String phone){
        List<CodeGenerator> codeByPhone = codeGeneratorDAO.getCodeByPhone(phone);
        return codeByPhone;
    }

    public void saveCode(CodeGenerator codeGenerator) {
        codeGenerator.setDate(returnDate());
        codeGeneratorDAO.saveOrUpdate(codeGenerator);
    }

    public CodeGeneratorDAO getCodeGeneratorDAO() {
        return codeGeneratorDAO;
    }

    public void setCodeGeneratorDAO(CodeGeneratorDAO codeGeneratorDAO) {
        this.codeGeneratorDAO = codeGeneratorDAO;
    }

}
