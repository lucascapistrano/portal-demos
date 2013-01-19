package ch.rasc.portaldemos.echat;

import java.util.Map;

import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;

import com.github.flowersinthesand.portal.Data;
import com.github.flowersinthesand.portal.Fn;
import com.github.flowersinthesand.portal.Handler;
import com.github.flowersinthesand.portal.Name;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Reply;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.google.common.collect.Maps;

@Handler("/echat")
public class EnhancedChatHandler {

	private final UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();

	private final Map<String, UserConnection> socketIdToUserMap = Maps.newConcurrentMap();

	private final Map<String, Socket> usernameToSocketMap = Maps.newConcurrentMap();

	@Name("echat")
	Room room;

	@On.close
	public void close(Socket socket) {
		disconnect(socket, null);
	}

	@On("disconnect")
	public void disconnect(Socket socket, @Reply Fn.Callback reply) {
		UserConnection uc = socketIdToUserMap.remove(socket.param("id"));
		if (uc != null) {
			room.send("disconnected", uc);
			usernameToSocketMap.remove(uc.getUsername());
		}

		if (reply != null) {
			reply.call();
		}
	}

	@On("connect")
	public void connect(Socket socket, @Data UserConnection newUser, @Reply Fn.Callback reply) {

		UserAgent ua = parser.parse(newUser.getBrowser());
		if (ua != null) {
			newUser.setBrowser(ua.getName() + " " + ua.getVersionNumber().toVersionString());
		}
		socketIdToUserMap.put(socket.param("id"), newUser);
		usernameToSocketMap.put(newUser.getUsername(), socket);

		room.send("connected", newUser);
		reply.call();
	}

	@On.open
	public void open(Socket socket) {
		room.add(socket);
		socket.send("connectedUsers", socketIdToUserMap.values());
	}

	@On.message
	public void message(@Data ChatMessage message) {
		room.send("message", message);
	}

	@On("sendSdp")
	public void sendSdp(@Data Map<String, Object> offerObject) {
		String toUsername = (String) offerObject.get("toUsername");
		Socket peerSocket = usernameToSocketMap.get(toUsername);
		if (peerSocket != null) {
			peerSocket.send("receiveSdp", offerObject);
		}
	}

	@On("sendIceCandidate")
	public void sendIceCandidate(@Data Map<String, Object> candidate) {
		String toUsername = (String) candidate.get("toUsername");
		Socket peerSocket = usernameToSocketMap.get(toUsername);
		if (peerSocket != null) {
			peerSocket.send("receiveIceCandidate", candidate);
		}
	}

}
