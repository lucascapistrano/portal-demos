package ch.rasc.portaldemos;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.atmosphere.cpr.AtmosphereHandler;
import org.atmosphere.cpr.AtmosphereServlet;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Initializer;

@WebServlet(urlPatterns = { "/sch", "/chat" }, loadOnStartup = 0)
public class PortalServlet extends AtmosphereServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc);

		try {
			Initializer i = new Initializer().init("ch.rasc.portaldemos.chat", "ch.rasc.portaldemos.scheduler");
			for (App app : i.apps().values()) {
				getServletContext().setAttribute("com.github.flowersinthesand.portal.App#" + app.name(), app);
				framework.addAtmosphereHandler(app.name(), (AtmosphereHandler) app.socketManager());
			}
		} catch (IOException e) {
			logger.error("Failed to scan the class path", e);
		}
	}
}