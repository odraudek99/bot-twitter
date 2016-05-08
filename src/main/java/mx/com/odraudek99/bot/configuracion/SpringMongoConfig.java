package mx.com.odraudek99.bot.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
@ComponentScan("mx.com.odraudek99.bot.*")
@PropertySource({"file:/home/odraudek99/github/properties/bot-twitter.properties" })
public class SpringMongoConfig extends AbstractMongoConfiguration {



	@Value("${mongo.url}")
	String mongoUrl;

	@Value("${mongo.db}")
	String mongoDb;
	
	@Override
	public String getDatabaseName() {
		return mongoDb;
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception {
		
		
		return new MongoClient(
				  new MongoClientURI(mongoUrl)
				);
		
		
//		return new MongoClient(MONGO_HOST, MONGO_PORT);
	}
	

	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}