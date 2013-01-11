package ch.rasc.portaldemos.twitter;

import java.util.List;

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
		
		List<Tweet> lastTweets = (List<Tweet>)room.get(TwitterReader.LAST_RECEIVED_TWEETS_KEY);
		if (lastTweets != null) {
			socket.send("newTweets", lastTweets);
		}
	}

}
