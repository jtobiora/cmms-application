package ng.upperlink.nibss.cmms.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

//@Service
public class NotificationConfig {
//
//    @Autowired
//    private JavaMailSender sender;
//
//    public void mailSender(String password,String emailAddress) throws AddressException, MessagingException, IOException {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
//
//        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("ugogbuzue1@gmail.com", "gma514801Reg_Num");
//            }
//        });
//        Message msg = new MimeMessage(session);
//        msg.setFrom(new InternetAddress("ugogbuzue1@gmail.com", false));
//
//        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));
//        msg.setSubject("Login credentials");
//        msg.setContent("Password: " + password, "text/html");
//        msg.setSentDate(new Date());
//
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        messageBodyPart.setContent("Password: " + password, "text/html");
//
//        Multipart multipart = new MimeMultipart();
//        multipart.addBodyPart(messageBodyPart);
//        MimeBodyPart attachPart = new MimeBodyPart();
//
//        //attachPart.attachFile("/var/tmp/image19.png");
//        //multipart.addBodyPart(attachPart);
//        msg.setContent(multipart);
//        Transport.send(msg);
//
//    }
}
