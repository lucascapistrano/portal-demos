import ch.rasc.embeddedtc.EmbeddedTomcat;

public class PortalDemosStartTomcat {
	public static void main(String[] args) throws Exception {
		EmbeddedTomcat.create().setPort(8081).startAndWait();
	}
}