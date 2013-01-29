package ch.rasc.portaldemos.grid;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;
import com.github.flowersinthesand.portal.atmosphere.AtmosphereModule;

@WebListener
public class GridInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		new App(new Options().url("/grid").packageOf(this), new AtmosphereModule(event.getServletContext())).register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing here
	}

}