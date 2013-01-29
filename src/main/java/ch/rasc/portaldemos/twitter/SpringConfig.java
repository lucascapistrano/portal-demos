package ch.rasc.portaldemos.twitter;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;
import com.github.flowersinthesand.portal.atmosphere.AtmosphereModule;
import com.github.flowersinthesand.portal.spring.SpringModule;

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
		return new App(new Options().url("/chat").packageOf(this), new AtmosphereModule(servletContext), new SpringModule(beanFactory));
	}

}
