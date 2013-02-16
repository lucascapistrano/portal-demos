package ch.rasc.portaldemos.twitter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;

@Bean
@Component
public class TwitterHandler {

	@Wire
	Room hall;

	@On
	public void open(Socket socket) {
		List<Tweet> lastTweets = (List<Tweet>) hall.get(TwitterReader.LAST_RECEIVED_TWEETS_KEY);
		if (lastTweets != null) {
			socket.send("newTweets", lastTweets);
		}
	}

}
