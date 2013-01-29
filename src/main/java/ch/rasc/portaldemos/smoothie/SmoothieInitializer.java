package ch.rasc.portaldemos.smoothie;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Options;
import com.github.flowersinthesand.portal.atmosphere.AtmosphereModule;

@WebListener
public class SmoothieInitializer implements ServletContextListener {

	static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		new App(new Options().url("/smoothie").packageOf(this), new AtmosphereModule(event.getServletContext()))
				.register();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		threadPool.shutdownNow();
	}

}