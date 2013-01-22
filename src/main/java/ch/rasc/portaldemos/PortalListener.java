package ch.rasc.portaldemos;

import javax.servlet.annotation.WebListener;

import com.github.flowersinthesand.portal.Options;
import com.github.flowersinthesand.portal.atmosphere.InitializerListener;

@WebListener
public class PortalListener extends InitializerListener {

	@Override
	protected void configure(Options options) {
		options.packages("ch.rasc.portaldemos.chat", "ch.rasc.portaldemos.scheduler", "ch.rasc.portaldemos.twitter",
				"ch.rasc.portaldemos.snake", "ch.rasc.portaldemos.grid", "ch.rasc.portaldemos.tail",
				"ch.rasc.portaldemos.echat", "ch.rasc.portaldemos.smoothie", "ch.rasc.portaldemos.map");
	}

}