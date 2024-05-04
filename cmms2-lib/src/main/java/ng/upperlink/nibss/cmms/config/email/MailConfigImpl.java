package ng.upperlink.nibss.cmms.config.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class MailConfigImpl implements MailConfig {

	@Value("${email_host}")
	private String host;

	@Value("${email_port}")
	private String port;

	@Value("${email_tls}")
	private String tls;

	@Value("${email_from}")
	private String from;

	@Value("${email_username}")
	private String username;

	@Value("${email_password}")
	private String password;

	@Override
	public Session getSession() {
		
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		};
		
		Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.from", from);
		properties.setProperty("mail.smtp.ssl.trust", "*");
        properties.setProperty("mail.smtp.starttls.enable", tls);

		Session session = Session.getInstance(properties, auth);
		return session;
	}

}
