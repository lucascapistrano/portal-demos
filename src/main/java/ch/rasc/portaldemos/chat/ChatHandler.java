package ch.rasc.portaldemos.chat;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.Data;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;

@Bean
public class ChatHandler {

	@Wire("chat")
	Room room;

	@On.close
	public void close(Socket socket) {
		System.out.println("closing: " + socket);
	}

	@On.open
	public void open(Socket socket) {
		room.add(socket);
	}

	@On.message
	public void message(@Data ChatMessage message) {
		room.send("message", message);
	}

}
