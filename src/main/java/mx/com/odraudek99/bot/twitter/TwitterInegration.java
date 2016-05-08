package mx.com.odraudek99.bot.twitter;

import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import mx.com.odraudek99.bot.correo.MailService;
import mx.com.odraudek99.bot.mongo.Persona;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@Component
public class TwitterInegration {

	
	 private static final Logger logger = LogManager.getLogger(TwitterInegration.class);
	
	@Value("${twitter.consumerKey}")
	String consumerKey;

	@Value("${twitter.consumerSecret}")
	String consumerSecret;

	@Value("${twitter.token}")
	String token;

	@Value("${twitter.tokenSecret}")
	String tokenSecret;

	@Autowired
	MailService mailService;

	@Value("${twitts.busqueda}")
	Integer twittsBusqueda;

	@Value("${dias.diferencia}")
	Integer diasDiferencia;

	@Autowired
	MongoOperations mongoTemplate;

	@Value("#{'${lista.mensajes}'.split(',')}")
	private List<String> mensajes;

	@Value("${twitter.query}")
	String twitterQuery;

	final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;

	int iMensaje;

	private void validaMensajes() {

		for (String mensaje : mensajes) {
			if (mensaje.length() > 128) {
				logger.info("Se omite el mensaje: " + mensaje);
				mensajes.remove(mensaje);
			} else {
				logger.info("Correcto tamaño el mensaje: " + mensaje);
			}
		}
	}

	private String getMensaje() {
		try {
			return mensajes.get(iMensaje);
		} catch (IndexOutOfBoundsException e) {
			iMensaje = 0;
		}
		return mensajes.get(iMensaje);
	}

	private void imprimirProperties() {
		
		logger.info("twitterQuery: " + twitterQuery);
		logger.info("twittsBusqueda: " + twittsBusqueda);
		logger.info("diasDiferencia: " + diasDiferencia);

	}

	public void init() {

		validaMensajes();
		imprimirProperties();

		try {

			Twitter twitter = TwitterFactory.getSingleton();

			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			AccessToken accessToken = new AccessToken(token, tokenSecret);

			twitter.setOAuthAccessToken(accessToken);
			twitter.verifyCredentials();

			logger.info("Usuario actual: " + twitter.getScreenName());

			Query query = new Query(twitterQuery);
			query.setCount(twittsBusqueda);
			QueryResult result = twitter.search(query);

			List<Status> listStatus = result.getTweets();

			logger.info("Cantidad status: " + listStatus.size());

			org.springframework.data.mongodb.core.query.Query searchUserQuery = null;

			for (Status status : listStatus) {

				logger.info(
						status.getUser().getScreenName() + ":" + status.getText() + " |" + status.getCreatedAt());

				searchUserQuery = new org.springframework.data.mongodb.core.query.Query(
						Criteria.where("idTwitter").is(status.getUser().getId()));

				Persona persona = mongoTemplate.findOne(searchUserQuery, Persona.class);

				if (persona == null) {
					logger.info("Enviando un twitt a: " + status.getUser().getScreenName() + ", " + getMensaje());
					// twitter.updateStatus("Enviando un twitt a:
					// "+status.getUser().getScreenName());

					persona = new Persona(status.getUser().getScreenName());
					persona.setIdTwitter(status.getUser().getId());
					persona.setFechaResponse(new Date());
					persona.getTwitts().add(status.getText());
					mongoTemplate.save(persona);
				} else {
					long diferencia = (persona.getFechaResponse().getTime() - status.getCreatedAt().getTime())
							/ MILLSECS_PER_DAY;

					if (diferencia > diasDiferencia) {

						logger.info("Enviando un twitt a: " + status.getUser().getScreenName());

						// StatusUpdate stat= new StatusUpdate("");
						// stat.setInReplyToStatusId(status.getId());
						// twitter.updateStatus(stat);

						persona.setFechaResponse(new Date());
						persona.getTwitts().add(status.getText());
						mongoTemplate.save(persona);
					} 

				}
				iMensaje++;
			}

			mailService.enviarMensaje(result);

		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

}
