package ch.rasc.portaldemos.chat;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;

@WebListener
public class ChatInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		new App(new Options().url("/chat").packages("ch.rasc.portaldemos.chat").beans(event.getServletContext()))
				.register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing here
	}

}