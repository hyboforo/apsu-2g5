/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.g5.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author GEN-NTB-431
 */
public class MailSender {

    public void sendMailToGroup(String subject, String message, List<Members> recepient) {

        try {
            Session session = authenticateSender();
            InternetAddress[] recipientAddress = new InternetAddress[recepient.size()];
            int counter = 0;
            for (Members member : recepient) {
                 recipientAddress[counter] = new InternetAddress(member.getEmail().trim().replaceAll(" ", ""));
                 counter ++;
            }
            Message mail = new MimeMessage(session);
            mail.setFrom(new InternetAddress("apsu2g5@gmail.com"));
            mail.setRecipients(Message.RecipientType.TO, recipientAddress);
            mail.setSubject(subject);
            mail.setText(message);
            Transport.send(mail);
            System.out.println("Done");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMail(String subject, String message, String recepient) {

        try {
            Session session = authenticateSender();
            Message mail = new MimeMessage(session);
            mail.setFrom(new InternetAddress("hyboforo@gmail.com"));
            mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recepient));
            mail.setSubject(subject);
            mail.setText(message);
            Transport.send(mail);
            System.out.println("Done");
        } catch (MessagingException e) {
            System.out.println("Error "+e.getLocalizedMessage());
        }

    }

    public Session authenticateSender() {
        final String username = "apsu2g5@gmail.com";
        final String password = "apsu@2005";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.socketFactory.port", "587");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        return session;
    }

}
