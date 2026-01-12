package com.cloudkitchenbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final String fromEmail="jagadeshrparthiban@gmail.com";

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendOrderConfirmationMail(String toEmail, String sub, String body){
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setSubject(sub);
        msg.setText(body);
        msg.setTo(toEmail);

        mailSender.send(msg);
    }

    @Async
    public void sendOrderCancellationMail(String toEmail, double refund){
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setFrom(fromEmail);
        msg.setText("Order cancelled successfully. \n\nRs."+refund+" will be refunded within few hours.");
        msg.setSubject("ORDER CANCELLATION");

        mailSender.send(msg);
    }
}
