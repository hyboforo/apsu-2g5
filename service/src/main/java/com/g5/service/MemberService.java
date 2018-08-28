/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.g5.service;

import java.util.List;

/**
 *
 * @author GEN-NTB-431
 */
public class MemberService {

    MemberDAO memberDAO;

    public MemberDAO getMemberDAO() {
        return memberDAO;
    }

    public void setMemberDAO(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    public void saveMember(Members members) {
        memberDAO.saveOrUpdate(members);
    }
    
    public Members getMemberByID(int id){
        Members memberByID = memberDAO.getMemberByID(id);
        return memberByID;
    }

    public List<Members> getAllMembers() {
        List<Members> allMembers = memberDAO.getAllMembers();
        return allMembers;
    }
    
    public List<Members> getMemberByFirstName(String firstName){
        List<Members> memberByFristName = memberDAO.getMemberByFristName(firstName);
        return memberByFristName;
    }
    
    public List<Members> getMemberByLastName(String lastName){
        List<Members> memberByLastName = memberDAO.getMemberByLastName(lastName);
        return memberByLastName;
    }
    public List<Members> getMemberByPhone(String phone){
        List<Members> memberByPhone = memberDAO.getMemberByPhone(phone);
        return memberByPhone;
    }
    public List<Members> getMemberByCity(String city){
        List<Members> memberByCity = memberDAO.getMemberByCity(city);
        return memberByCity;
     
    }
    public List<Members> getMemberByCountry(String Country){
        List<Members> memberByCountry = memberDAO.getMemberByCountry(Country);
        return memberByCountry;
    }
    
    public List<Members> getMembersByRegion(String region){
        List<Members> memberByRegion = memberDAO.getMemberByRegion(region);
        return memberByRegion;
    }
    
    public List<Members> getMemberByCourseClass(String courseClass){
        List<Members> members = memberDAO.getMemberByCOurseClass(courseClass);
        return members;
        
    }
    
    public List<Members> getMemberByCourse(String course){
        List<Members> members = memberDAO.getMemberByCourse(course);
        return members;
        
    }
    
    public List<Members> getMemberByHouse(String house){
        List<Members> members = memberDAO.getMemberByHouse(house);
        return members;
        
    }
    

}
