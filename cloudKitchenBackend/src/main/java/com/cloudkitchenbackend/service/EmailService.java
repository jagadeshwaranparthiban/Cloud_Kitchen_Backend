package com.cloudkitchenbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final String fromEmail="jagadeshrparthiban@gmail.com";

    @Autowired
    private JavaMailSender mailSender;

    public String sendOrderConfirmationMail(String toEmail, String sub, String body){
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setSubject(sub);
        msg.setText(body);
        msg.setTo(toEmail);

        mailSender.send(msg);
        return "Mail sent to: "+toEmail;
    }
}
