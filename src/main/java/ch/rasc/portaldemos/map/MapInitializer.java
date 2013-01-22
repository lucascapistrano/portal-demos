package ch.rasc.portaldemos.map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;

@WebListener
public class MapInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		new App(new Options().url("/map").packages("ch.rasc.portaldemos.map").beans(event.getServletContext())).register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}

}