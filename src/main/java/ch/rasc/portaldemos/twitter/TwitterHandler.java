package ch.rasc.portaldemos.twitter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.Wire;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;

@Bean
@Component
public class TwitterHandler {

	@Wire("twitter")
	Room room;

	@On.open
	public void open(Socket socket) {
		room.add(socket);

		List<Tweet> lastTweets = (List<Tweet>) room.get(TwitterReader.LAST_RECEIVED_TWEETS_KEY);
		if (lastTweets != null) {
			socket.send("newTweets", lastTweets);
		}
	}

}
