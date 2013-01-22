package ch.rasc.portaldemos.tail;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.Wire;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;

@Bean
public class TailHandler {

	@Wire("tail")
	Room room;

	@On.open
	public void open(Socket socket) {
		room.add(socket);
	}

}
