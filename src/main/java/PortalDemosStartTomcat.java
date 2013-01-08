import ch.rasc.embeddedtc.EmbeddedTomcat;

public class PortalDemosStartTomcat {
	public static void main(String[] args) throws Exception {
		EmbeddedTomcat.create().startAndWait();
	}
}