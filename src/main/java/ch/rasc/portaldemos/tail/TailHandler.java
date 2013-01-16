package ch.rasc.portaldemos.tail;

import com.github.flowersinthesand.portal.Handler;
import com.github.flowersinthesand.portal.Name;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;

@Handler("/tail")
public class TailHandler {

	@Name("tail")
	Room room;

	@On.open
	public void open(Socket socket) {
		room.add(socket);
	}

}
