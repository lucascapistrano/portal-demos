package ch.rasc.portaldemos.twitter;

import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import ch.rasc.s4ws.twitter.Tweet;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;
import com.github.flowersinthesand.portal.atmosphere.AtmosphereModule;
import com.github.flowersinthesand.portal.spring.SpringModule;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = { "ch.rasc.portaldemos.twitter" })
public class SpringConfig {

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private ServletContext servletContext;

	@Bean
	public App app() {
		App app = new App(new Options().url("/twitter").packageOf(this), new AtmosphereModule(servletContext),
				new SpringModule(beanFactory));
		app.bean(TwitterHandler.class).init();
		return app;
	}

	@Bean
	public ITopic<Tweet> hazelcastTopic() {
		HazelcastInstance hc = Hazelcast.newHazelcastInstance();
		return hc.getTopic("tweets");
	}

	@PreDestroy
	public void destroy() {
		Hazelcast.shutdownAll();
	}

}
