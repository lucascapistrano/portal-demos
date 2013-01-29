package ch.rasc.portaldemos.twitter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;
import com.github.flowersinthesand.portal.atmosphere.AtmosphereModule;
import com.github.flowersinthesand.portal.spring.SpringModule;

@WebListener
public class SpringInitializer implements ServletContextListener {

	private AnnotationConfigApplicationContext ctx;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		new App(new Options().url("/twitter").packageOf(this), new AtmosphereModule(sce.getServletContext()), new SpringModule(ctx)).register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (ctx != null) {
			ctx.close();
		}
	}

}