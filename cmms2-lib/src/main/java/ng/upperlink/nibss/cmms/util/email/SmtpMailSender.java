package ng.upperlink.nibss.cmms.util.email;

import ng.upperlink.nibss.cmms.config.email.MailConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@PropertySource("classpath:application.properties")
public class SmtpMailSender {
	
	private Environment env;
	private MailConfig mailConfig;
	private EmailService emailService;

	@Value("${email-banner-image}")
	private String emailLogo;

	@Value("${poweredby}")
	private String poweredBy;
	
	@Autowired
	public SmtpMailSender(Environment env, MailConfig mailConfig, EmailService emailService) {
		this.env = env;
		this.mailConfig = mailConfig;
		this.emailService = emailService;
	}
	
	private static Logger logger = LoggerFactory.getLogger(SmtpMailSender.class);

	@Async
	public void sendMail(String from, String[] to, String subject, String title, String subtitle, String details) {

		MailContent mail = new MailContent();
		mail.setFrom(from);
		mail.setTo(to);
		mail.setSubject(subject);

		Map<String, Object> model = new HashMap<>();
		model.put("title", title);
		model.put("subtitle", subtitle);

		model.put("emailLogo", emailLogo);
		model.put("poweredBy", poweredBy);

		//Details from the module
		model.put("details", details);
		mail.setModel(model);

		try {
			emailService.sendSimpleMessage(mail);
		} catch (MessagingException | IOException e) {
			logger.error("Exception  occurred trying to send a mail", e);
		}
	}
}
