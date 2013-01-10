package ch.rasc.portaldemos.twitter;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = { "ch.rasc.portaldemos.twitter" })
public class SpringConfig {
	// nothing here
}
