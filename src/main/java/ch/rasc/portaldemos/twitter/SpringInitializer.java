package ch.rasc.portaldemos.twitter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebListener
public class SpringInitializer implements ServletContextListener {

	private AnnotationConfigApplicationContext ctx;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
		new App(new Options().url("/twitter").packages("ch.rasc.portaldemos.twitter").beans(event.getServletContext(), ctx)).register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (ctx != null) {
			ctx.close();
		}
	}

}