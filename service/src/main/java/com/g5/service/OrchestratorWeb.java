/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.g5.service;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author GEN-NTB-431
 */
@Controller
@SessionAttributes("token")
public class OrchestratorWeb {

    CodeGeneratorService codeGeneratorService;
    MemberService memberService;

    @Resource
    TransactionTemplate transactionTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrchestratorWeb.class);

    @RequestMapping({"/", "/index"})
    public String index() {
        System.out.println("Hi, i'm calling index");
        return "index";
    }

    @RequestMapping({"/", "/validate"})
    public String validate(CodeGenerator codeGenerator) {
        System.out.println("Hi there, i'm calling validate");
        return "validate";
    }

//    @RequestMapping({"/", "/register"})
//    public String register() {
//        System.out.println("in register");
//        return "register";
//    }
//
//    @RequestMapping(value = "/registerAction", method = RequestMethod.POST)
//    public String registerAction(Members members) {
//        memberService.saveMember(members);
//        return "readme";
//    }
    @RequestMapping({"/", "/createCode"})
    public String createCode(CodeGenerator codeGenerator) {
        return "createCode";
    }

    @RequestMapping(value = "/createCodeAction", method = RequestMethod.POST)
    public String createCodeAction(CodeGenerator codeGenerator) {
        try {

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus ts) {
                    try {
                        System.out.println("In createCodeAction");
                        String generateCode = codeGeneratorService.generateCode();
                        codeGenerator.setToken(generateCode);
                        System.out.println("token is " + generateCode);
                        codeGeneratorService.saveCode(codeGenerator);
                        System.out.println("Sending Mail");
                        MailSender mailer = new MailSender();
                        String subject = "Registration Token";
                        String message = "Thank you for Paying. Find your registration Token below.\n " + generateCode;
                        mailer.sendMail(subject, message, codeGenerator.getEmail());
                        System.out.println("Mail sent");
                        ts.flush();
                    } catch (Exception ex) {
                        logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
                    }
                }
            });
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }

        return "notification";
    }

    @RequestMapping(value = "/validateAction", method = RequestMethod.POST)
    public String validateAction(CodeGenerator codeGenerator, HttpSession session) {
        Members members = new Members();
        System.out.println("In validateAction");
        if (codeGenerator == null) {
            System.out.println("There was an error with validation");
            return "validate";
        }
        boolean doValidate = doValidate(codeGenerator.getToken());
        if (doValidate) {
            session.setAttribute("token", codeGenerator.getToken());
            return createUser(members, session);
        }
        return "validate";
    }

    public boolean doValidate(String token) {
        try {
            return transactionTemplate.execute(new TransactionCallback<Boolean>() {
                @Override
                public Boolean doInTransaction(TransactionStatus ts) {
                    boolean compareCode = codeGeneratorService.compareCode(token);
                    return compareCode;
                }
            });
        } catch (Exception ex) {
            logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
        }
        return false;
    }

    @RequestMapping({"/", "/createUser"})
    public String createUser(Members members, HttpSession session) {
        System.out.println("Hi there, i'm calling createUser");
        Object attribute = session.getAttribute("token");
        if (attribute == null) {
            return "index";
        }
        System.out.println("Session is " + session.getAttribute("token"));
        return "createUser";
    }

    @RequestMapping(value = "/createUserAction", method = RequestMethod.POST)
    public String createUserAction(Members members, HttpSession session) {
        Object attribute = session.getAttribute("token");
        if (attribute == null) {
            return "index";
        }
        try {

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus ts) {
                    try {
                        System.out.println("In createUserAction");
                        System.out.println("Saving member to database");
                        memberService.saveMember(members);
                        System.out.println("Sending Mail");
                        MailSender mailer = new MailSender();
                        String subject = "Thank You";
                        String message = "Thank you, your registration was completed successfully ";
                        mailer.sendMail(subject, message, members.getEmail());
                        System.out.println("Mail sent");
                        ts.flush();
                    } catch (Exception ex) {
                        logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
                    }
                }
            });
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }
        updateTokenStatus(attribute.toString());
        return "index";
    }
    
    public List<CodeGenerator> getAllCodes(){
        try {
            return transactionTemplate.execute(new TransactionCallback<List<CodeGenerator>>() {
                @Override
                public List<CodeGenerator> doInTransaction(TransactionStatus ts) {
                    List<CodeGenerator> allCodes = codeGeneratorService.getAllCodes();
                    return allCodes;
                }
            });
        } catch (Exception ex) {
            logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
        }
        return null;
        
    }
    
    @RequestMapping({"/", "/allcodes"})
    public String showAllCodes(Model model) {
        List<CodeGenerator> allCodes = getAllCodes();
        model.addAttribute("codes", allCodes);
        return "allcodes";

    }
    
    @RequestMapping({"/", "/codeSearch"})
    public String codeSearch(Model model, HttpServletRequest request) {
        List<CodeGenerator> allCodes = tokenSearch(request.getParameter("phone"));
        model.addAttribute("codes", allCodes);
        return "allcodes";
    }
    
    public List<CodeGenerator> tokenSearch(final String phone){
         try {
            return transactionTemplate.execute(new TransactionCallback<List<CodeGenerator>>() {
                @Override
                public List<CodeGenerator> doInTransaction(TransactionStatus ts) {
                    List<CodeGenerator> codeByPhone = codeGeneratorService.getCodeByPhone(phone);
                    return codeByPhone;
                }
            });
        } catch (Exception ex) {
            logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
        }
        return null;
        
    }
    
    
    
    
    public void updateTokenStatus(final String token){
        try {

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus ts) {
                    try {
                        CodeGenerator code = codeGeneratorService.getCode(token);
                        code.setStatus(1);
                        codeGeneratorService.saveCode(code);
                        ts.flush();
                    } catch (Exception ex) {
                        logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
                    }
                }
            });
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        } 
        
    }

//    @RequestMapping(value = "/validatetoken", params = {"save"})
//    public String saveSeedstarter(
//            final CodeGenerator codeGenerator, final BindingResult bindingResult, final ModelMap model) {
//        if (bindingResult.hasErrors()) {
//            return "validatetoken";
//        }
//        codeGeneratorService.compareCode(code);
//        model.clear();
//        return "redirect:/seedstartermng";
//    }
    @RequestMapping({"/", "/showall"})
    public String showAll(Model model) {
        List<Members> returnMembers = returnMembers();
        model.addAttribute("members", returnMembers);
        return "showall";

    }

    @RequestMapping({"/", "/searchAction"})
    public String searchAction(Model model, HttpServletRequest request, HttpServletResponse response) {
        List<Members> returnMembers = searchForMembers(request.getParameter("criteria"), request.getParameter("param"), request.getParameter("param2"));
        model.addAttribute("members", returnMembers);
        return "showall";

    }

    public List<Members> returnMembers() {
        try {
            return transactionTemplate.execute(new TransactionCallback<List<Members>>() {
                @Override
                public List<Members> doInTransaction(TransactionStatus ts) {
                    List<Members> allMembers = memberService.getAllMembers();
                    return allMembers;
                }
            });
        } catch (Exception ex) {
            logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
        }
        return null;
    }

    public List<Members> searchForMembers(final String criteria, final String params, String params2) {
        try {
            return transactionTemplate.execute(new TransactionCallback<List<Members>>() {
                @Override
                public List<Members> doInTransaction(TransactionStatus ts) {
                    List<Members> members = null;
                    if (criteria.equalsIgnoreCase("city")) {
                        members = memberService.getMemberByCity(params2);
                        return members;
                    }
                    if (criteria.equalsIgnoreCase("region")) {
                        members = memberService.getMembersByRegion(params2);
                        return members;
                    }
                    if (criteria.equalsIgnoreCase("country")) {
                        members = memberService.getMemberByCountry(params2);
                        return members;
                    }
                    if (criteria.equalsIgnoreCase("First Name")) {
                        members = memberService.getMemberByFirstName(params2);
                        return members;
                    }
                    if (criteria.equalsIgnoreCase("Last Name")) {
                        members = memberService.getMemberByLastName(params2);
                        return members;

                    }
                    if (criteria.equalsIgnoreCase("Phone")) {
                        members = memberService.getMemberByPhone(params2);
                        return members;
                    }
                    if (criteria.equalsIgnoreCase("Class")) {
                        members = memberService.getMemberByCourseClass(params);
                        return members;
                    }
                    if (criteria.equalsIgnoreCase("Course")) {
                        members = memberService.getMemberByCourse(params);
                        return members;
                    }
                    if (criteria.equalsIgnoreCase("House")) {
                        members = memberService.getMemberByHouse(params);
                        return members;
                    }
                    return null;
                }
            });
        } catch (Exception ex) {
            logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
        }
        return null;
    }
    
    @RequestMapping({"/", "/displayAction"})
    public String displayAction(Model model, HttpServletRequest request, HttpServletResponse response) {
        String parameter = request.getParameter("id");
        int result = Integer.parseInt(parameter);
        Members memberDetails = getMemberDetails(result);
        model.addAttribute("members", memberDetails);
        return "viewmember";

    }

    public Members getMemberDetails(int param) {
        try {
            return transactionTemplate.execute(new TransactionCallback<Members>() {
                @Override
                public Members doInTransaction(TransactionStatus ts) {
                    Members allMembers = memberService.getMemberByID(param);
                    return allMembers;
                }
            });
        } catch (Exception ex) {
            logger.error("Failed to process subjects within transaction: " + ex.getLocalizedMessage());
        }
        return null;

    }

    public CodeGeneratorService getCodeGeneratorService() {
        return codeGeneratorService;
    }

    public void setCodeGeneratorService(CodeGeneratorService codeGeneratorService) {
        this.codeGeneratorService = codeGeneratorService;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

}
