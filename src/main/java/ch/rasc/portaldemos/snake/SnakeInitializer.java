package ch.rasc.portaldemos.snake;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;

@WebListener
public class SnakeInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		new App(new Options().url("/snake").packages("ch.rasc.portaldemos.snake").beans(event.getServletContext())).register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}

}