package com.arenaedge.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private final Session session;
    private final String senderEmail;
    
    // Create with default GreenMail configuration for testing
    public EmailService() {
        this("arenaedge@gmail.com", "localhost", 3025);
    }
    
    // Create with custom configuration
    public EmailService(String senderEmail, String smtpHost, int smtpPort) {
        this.senderEmail = senderEmail;
        
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.auth", "true");
        
        // Create the session
        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("test", "test");
            }
        });
    }
    
    // Send an email
    public boolean sendEmail(String recipientEmail, String subject, String body) {
        try {
            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);
            
            // Send the message
            Transport.send(message);
            
            System.out.println("Email sent successfully to: " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}