package ch.rasc.portaldemos.echat;

import java.util.Map;

import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import com.github.flowersinthesand.portal.Data;
import com.github.flowersinthesand.portal.Handler;
import com.github.flowersinthesand.portal.Name;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.google.common.collect.Maps;

@Handler("/echat")
public class EnhancedChatHandler {

	private final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

	private final Map<String, UserConnection> connectedUsers = Maps.newConcurrentMap();

	@Name("echat")
	Room room;

	@On.close
	public void close(Socket socket) {
		System.out.println("closing: " + socket);
		connectedUsers.remove(socket.param("id"));
	}

	@On("connect")
	public void connect(Socket socket, @Data UserConnection newUser) {

		UserAgent ua = parser.parse(newUser.getBrowser());
		if (ua != null) {
			newUser.setBrowser(ua.getName() + " " + ua.getVersionNumber().toVersionString());
		}
		connectedUsers.put(socket.param("id"), newUser);

		room.send("connected", newUser);
	}

	@On.open
	public void open(Socket socket) {
		room.add(socket);
		socket.send("connectedUsers", connectedUsers.values());
	}

	@On.message
	public void message(@Data ChatMessage message) {
		room.send("message", message);
	}

}
