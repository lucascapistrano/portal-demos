package ch.rasc.portaldemos.echat;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;

@WebListener
public class EnhancedChatInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		new App(new Options().url("/echat").packages("ch.rasc.portaldemos.echat").beans(event.getServletContext()))
				.register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing here
	}

}