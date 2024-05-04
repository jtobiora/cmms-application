package ng.upperlink.nibss.cmms.util.email;

import lombok.extern.slf4j.Slf4j;
import ng.upperlink.nibss.cmms.enums.RoleName;
import ng.upperlink.nibss.cmms.enums.UserType;
import ng.upperlink.nibss.cmms.model.User;
import ng.upperlink.nibss.cmms.model.bank.BankUser;
import ng.upperlink.nibss.cmms.model.biller.BillerUser;
import ng.upperlink.nibss.cmms.model.mandate.Mandate;
import ng.upperlink.nibss.cmms.service.UserService;
import ng.upperlink.nibss.cmms.service.bank.BankUserService;
import ng.upperlink.nibss.cmms.service.biller.BillerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MailUtility {
    @Autowired
    private UserService userService;
    @Autowired
    private BillerUserService billerUserService;
    @Autowired
    private BankUserService bankUserService;
    @Autowired
    private SpringTemplateEngine templateEngine;

    private String salt;
    @Value("${email_from}")
    private String fromEmail;
    @Value("${email-banner-image}")
    private String emailLogo;

    @Value("${poweredby}")
    private String poweredBy;

    public enum MailType {
        NEW_MANDATE,
        MANDATE_AUTHORIZED,
        MANDATE_APPROVED,
        MANDATE_REJECTED,
        MANDATE_ACCEPTED,

        BILLER_AUTHORIZED_MANDATES,
        BILLER_REJECTED_MANDATES
    }

    public synchronized void sendNotificationMail(MailType mailType, Mandate mandate, User user, boolean isUpdate) {
        switch (mailType) {
            case NEW_MANDATE:
                mandateInitiated(mandate, user, isUpdate);
                break;
            case MANDATE_AUTHORIZED:
                mandateAuthorized(mandate, user);
                break;
            case MANDATE_REJECTED:
                mandateRejected(mandate, user);
                break;
            case MANDATE_ACCEPTED:
                mandateAuthorized(user, mandate);
                break;
            case MANDATE_APPROVED:
                mandateApproved(user, mandate);
                break;

            case BILLER_AUTHORIZED_MANDATES:
                billerAuthorized(mandate,user);
                break;
            case BILLER_REJECTED_MANDATES:
                billerRejectedMandate(mandate,user);
                break;
            default:

        }
    }

    public void composeMail(String[] emailArr, Mandate mandate, String subject,
                            String title, String userToAct, String message) {
        if (emailArr.length > 0) {
            this.prepareMail(fromEmail, emailArr, subject,
                    title, message, generateDetails(mandate, userToAct));
        }
    }

    @Async
    public synchronized void prepareMail(String from, String[] to, String subject, String title, String message, String mandateDetails) {

        MailContent mail = new MailContent();
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject(subject);

        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("message", message);

        model.put("emailLogo", emailLogo);
        model.put("poweredBy", poweredBy);

        //Details from the module
        model.put("details", mandateDetails);
        mail.setModel(model);

        try {
            this.sendMail(mail);
        } catch (MessagingException | IOException e) {
            log.error("Exception  occurred trying to send a mail", e);
        }
    }

    private String generateDetails(Mandate mandate, String userToActOnMandate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");
        String details = "";
        details += "<strong>Mandate Reference Code :</strong>&nbsp;&nbsp;" + mandate.getMandateCode() + "<br/>";
        details += "<strong>Subscriber Code :</strong> &nbsp;" + mandate.getSubscriberCode() + "<br/>";
        details += "<strong>Product :</strong>&nbsp; " + mandate.getProduct().getName() + "<br/>";
        details += "<strong>Customer bank :</strong>&nbsp;" + mandate.getBank().getName() + "<br/>";
        details += "<strong>Fixed Amount :</strong>&nbsp;" + mandate.getAmount() + "<br/>";
        details += "<strong>Mandate Validity:</strong>&nbsp;" + String.format("From %s to %s", sdf.format(mandate.getStartDate()), sdf.format(mandate.getEndDate())) + "<br/>";
        details += "<br/>";

        return details;
    }

    private synchronized void mandateAuthorized(Mandate mandate, User user) {
        List<String> emailList = new ArrayList<>();
        String userToActOnMandate = null;
        String[] userEmails = null;
        String mailMessage = "";
        mailMessage += "This is to notify you that the mandate with the details below has been authorized on Central Mandate Management System (CMMS) and it requires your attention.";

       if (mandate.getAcceptedBy().getUserType() == UserType.BANK) {
            //notify all BANK_INITIATORS
            BankUser bankUser = (BankUser) user;
            List<BankUser> userList = bankUserService.getUsersByUserType(UserType.BANK, true, RoleName.BANK_INITIATOR, bankUser.getUserBank());
            if(!userList.isEmpty()){
                emailList = userList.stream().map(User::getEmailAddress).collect(Collectors.toList());
                userEmails = emailList.toArray(new String[emailList.size()]);
                userToActOnMandate = "Bank Initiator";
                mailMessage += String.format(" A %s is to login to CMMS Portal and act on the mandate. Thank you.", userToActOnMandate);
                this.composeMail(userEmails, mandate, "Central Mandate Management System (CMMS) Notification Service", "Mandate Authorization", userToActOnMandate, mailMessage);

            }
        }
    }

    private synchronized void billerAuthorized(Mandate mandate, User user) {
        List<String> emailList = new ArrayList<>();
        String userToActOnMandate = null;
        String[] userEmails = null;
        String mailMessage = "";
        mailMessage += "This is to notify you that the mandate with the details below has been authorized on Central Mandate Management System (CMMS) and it requires your attention.";

        if (mandate.getAuthorizedBy().getUserType() == UserType.BILLER) {
            //notify all BANK_INITIATORs
            BillerUser bUser = (BillerUser) user;
            List<BankUser> userList = bankUserService.getUsersByUserType(UserType.BANK, true, RoleName.BANK_INITIATOR, bUser.getBiller().getBank());
            if(!userList.isEmpty()) {
                emailList = userList.stream().map(User::getEmailAddress).collect(Collectors.toList());
                userEmails = emailList.toArray(new String[emailList.size()]);
                userToActOnMandate = "Bank Initiator";
                mailMessage += String.format(" A %s is to login to CMMS Portal and act on the mandate. Thank you.", userToActOnMandate);
                this.composeMail(userEmails, mandate, "Central Mandate Management System (CMMS) Notification Service", "Mandate Authorization", userToActOnMandate, mailMessage);
            }

        }
    }

    private synchronized void mandateAuthorized(User user, Mandate mandate) {
        List<String> emailList = new ArrayList<>();
        String userToActOnMandate = null;
        String[] userEmails = null;

        BankUser bankUser = (BankUser) user;
        List<BankUser> userList = bankUserService.getUsersByUserType(UserType.BANK, true, RoleName.BANK_AUTHORIZER, bankUser.getUserBank());
        if (!userList.isEmpty()) {
            emailList = userList.stream().map(User::getEmailAddress).collect(Collectors.toList());
            userEmails = emailList.toArray(new String[emailList.size()]);
            userToActOnMandate = "Bank Users";

            String mailMessage = "This is to notify you that the mandate with the details below has been accepted on Central Mandate Management System (CMMS) and it requires your attention.";
            this.composeMail(userEmails, mandate, "Central Mandate Management System (CMMS) Notification Service", "Mandate Authorization", userToActOnMandate, mailMessage);

        }
    }

    private synchronized void mandateApproved(User user, Mandate mandate) {
        List<String> emailList = null;
        String userToNotify = null;
        String[] userEmails = null;

        BankUser bankUser = (BankUser) user;
        List<BankUser> userList = bankUserService.getUsersByUserType(UserType.BANK, true, RoleName.BANK_AUTHORIZER, bankUser.getUserBank());
        if (!userList.isEmpty()) {
            emailList = userList.stream().map(User::getEmailAddress).collect(Collectors.toList());
            userEmails = emailList.toArray(new String[emailList.size()]);
            userToNotify = "Bank Authorizer";

            String mailMessage = "This is to notify you that the mandate with the details below has been finally approved on Central Mandate Management System (CMMS). Consequently, a mandate advice has been sent. Thank you.";
            this.composeMail(userEmails, mandate, "Central Mandate Management System (CMMS) Notification Service", "Mandate Approval", userToNotify, mailMessage);

        }
    }

    private synchronized void mandateInitiated(Mandate mandate, User user, boolean isUpdate) {
        List<String> emailList = new ArrayList<>();
        String userToActOnMandate = null;
        String[] userEmails = null;

        String message = "";
        message += String.format("A new mandate has been set up for %s and it requires your attention. Please find below the details of the mandate.", mandate.getBiller().getName().toUpperCase());

        if (!isUpdate) {
            if (mandate.getCreatedBy().getUserType() == UserType.BILLER) {
                //notify all BILLER_AUTHORIZERs
                BillerUser billerUser = (BillerUser) user;
                List<BillerUser> userList = billerUserService.getUsersByUserType(UserType.BILLER, true, RoleName.BILLER_AUTHORIZER, billerUser.getBiller());
                if (!userList.isEmpty()) {
                    emailList = userList.stream().map(User::getEmailAddress).collect(Collectors.toList());
                    userEmails = emailList.toArray(new String[emailList.size()]);
                    userToActOnMandate = "Biller Authorizer";
                    message += String.format(" A %s is to login to CMMS Portal and act on the mandate. Thank you.", userToActOnMandate);
                    this.composeMail(userEmails, mandate, "New Mandate Created", "Central Mandate Management System (CMMS) Notification Service", userToActOnMandate, message);
                }


            } else if (mandate.getCreatedBy().getUserType() == UserType.BANK) {
                //notify all BANK_BILLER_AUTHORIZERs
                BankUser bankUser = (BankUser) user;
                List<BankUser> userList = bankUserService.getUsersByUserType(UserType.BANK, true, RoleName.BANK_BILLER_AUTHORIZER, bankUser.getUserBank());
                if (!userList.isEmpty()) {
                    emailList = userList.stream().map(User::getEmailAddress).collect(Collectors.toList());
                    userEmails = emailList.toArray(new String[emailList.size()]);
                    userToActOnMandate = "Bank Biller Authorizer";
                    message += String.format(" A %s is to login to CMMS Portal and act on the mandate. Thank you.", userToActOnMandate);
                    this.composeMail(userEmails, mandate, "New Mandate Created", "Central Mandate Management System (CMMS) Notification Service", userToActOnMandate, message);
                }
            }
        }
    }

    private synchronized void mandateRejected(Mandate mandate, User user) {
        List<String> emailList = new ArrayList<>();
        String userToActOnMandate = null;
        String[] userEmails = null;
        String mailMessage = "";
        mailMessage += "This is to notify you that the mandate with the details below has been rejected on Central Mandate Management System (CMMS).";

        if (mandate.getLastActionBy().getUserType() == UserType.BANK) {
            BankUser bUser = (BankUser) user;
            Optional<RoleName> role = bUser.getRoles().stream().map(r -> r.getName()).findAny();
            List<BankUser> userList = bankUserService.getUsersByUserType(UserType.BILLER, true, role.get(), bUser.getUserBank());

            if(!userList.isEmpty()){
                emailList = userList.stream().map(User::getEmailAddress).collect(Collectors.toList());
                userEmails = emailList.toArray(new String[emailList.size()]);
                userToActOnMandate = "users";
                this.composeMail(userEmails, mandate, "Mandate Rejection", "Central Mandate Management System (CMMS) Notification Service", userToActOnMandate, mailMessage);

            }

        }
    }

    private synchronized void billerRejectedMandate(Mandate mandate, User user) {
        List<String> emailList = new ArrayList<>();
        String userToActOnMandate = null;
        String[] userEmails = null;
        String mailMessage = "";
        mailMessage += "This is to notify you that the mandate with the details below has been rejected on Central Mandate Management System (CMMS).";

        if (mandate.getLastActionBy().getUserType() == UserType.BILLER) {
            //get all the users whose role is BILLER_AUTHORIZER
            BillerUser bUser = (BillerUser) user;
            List<BillerUser> userList = billerUserService.getUsersByUserType(UserType.BILLER, true, RoleName.BILLER_AUTHORIZER, bUser.getBiller());

            if(!userList.isEmpty()){
                emailList = userList.stream().map(User::getEmailAddress).collect(Collectors.toList());
                userEmails = emailList.toArray(new String[emailList.size()]);
                userToActOnMandate = "Biller Authorizer";
                this.composeMail(userEmails, mandate, "Mandate Rejection", "Central Mandate Management System (CMMS) Notification Service", userToActOnMandate, mailMessage);
            }

        }
    }

    private synchronized void sendMail(MailContent mail) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "pod51017.outlook.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "*");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("no-reply@nibss-plc.com.ng", "09ytrewq*");
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("no-reply@nibss-plc.com.ng", false));

        String emails = String.join(",", mail.getTo());
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emails));

        // msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(""));
        Context context = new Context();
        context.setVariables(mail.getModel());
        String html = templateEngine.process("mandate-template", context);
        msg.setSubject(mail.getSubject());
        msg.setContent(html, "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(html, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

        //attachPart.attachFile("/var/tmp/image19.png");
        //multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);

    }


}


