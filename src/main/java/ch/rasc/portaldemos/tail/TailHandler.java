package ch.rasc.portaldemos.tail;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;

@Bean
public class TailHandler {

	@Wire
	Room room;

	@On.open
	public void open(Socket socket) {
		room.add(socket);
	}

}
