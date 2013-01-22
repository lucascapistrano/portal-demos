package ch.rasc.portaldemos.scheduler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;

@WebListener
public class SchedulerInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		new App(new Options().url("/sch").packages("ch.rasc.portaldemos.scheduler").beans(event.getServletContext())).register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}

}