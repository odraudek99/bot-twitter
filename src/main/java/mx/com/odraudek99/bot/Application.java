package mx.com.odraudek99.bot;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import mx.com.odraudek99.bot.configuracion.SpringMongoConfig;
import mx.com.odraudek99.bot.twitter.TwitterInegration;
import twitter4j.TwitterException;



public class Application {

	

	public static void main(String[] args) throws IOException, TwitterException {
		

		ApplicationContext context = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		
		
		TwitterInegration twitterInegration = (TwitterInegration) context.getBean("twitterInegration");
		twitterInegration.init();

	}

}
