package ch.rasc.portaldemos;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class RealPathTest implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String realPath = sce.getServletContext().getRealPath("/WEB-INF/classes/");
		System.out.println("/WEB-INF/classes/ : " + realPath);

		realPath = sce.getServletContext().getRealPath("/WEB-INF/lib/");
		System.out.println("/WEB-INF/lib/ : " + realPath);

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing here
	}

}
