package ch.rasc.portaldemos.twitter;

import com.github.flowersinthesand.portal.Handler;
import com.github.flowersinthesand.portal.Name;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;

@Handler("/twitter")
public class TwitterHandler {

	@Name("twitter")
	Room room;

	@On.open
	public void open(Socket socket) {
		room.add(socket);
	}

}
