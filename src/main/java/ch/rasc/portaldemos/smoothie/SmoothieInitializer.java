package ch.rasc.portaldemos.smoothie;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;

@WebListener
public class SmoothieInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		new App(new Options().url("/smoothie").packages("ch.rasc.portaldemos.smoothie")
				.beans(event.getServletContext())).register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing here
	}

}