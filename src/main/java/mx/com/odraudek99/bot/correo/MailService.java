package mx.com.odraudek99.bot.correo;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import twitter4j.QueryResult;
import twitter4j.Status;

@Component
public class MailService {


	 private static final Logger logger = LogManager.getLogger(MailService.class);
	 
	@Value("${mail.password}")
	String password;

	@Value("${mail.correoSalida}")
	String correoSalida;

	@Value("${mail.correoDestino}")
	String correoDestino;

	public int enviarMensaje(QueryResult result) {

		StringBuffer sb = new StringBuffer();

		for (Status status : result.getTweets()) {
			sb.append("---------\n");
			sb.append(status.getCreatedAt() + " @" + status.getUser().getScreenName() + ":" + status.getText() + "\n");
		}

		logger.info("correoSalida: " + correoSalida);

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(correoSalida, password);
			}
		});

		try {
			logger.info("session: " + session);
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(correoSalida));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
			message.setSubject("Consulta Twitter");
			message.setText(sb.toString());

			Transport.send(message);

			logger.info("Done");

		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return 0;
	}

}
