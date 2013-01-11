package ch.rasc.portaldemos.twitter;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.github.flowersinthesand.portal.App;
import com.github.flowersinthesand.portal.Room;
import com.google.common.collect.Lists;

@Service
public class TwitterReader {
	public final static String LAST_RECEIVED_TWEETS_KEY = "LAST_RECEIVED_TWEETS";

	private long sinceId = 0;

	@Scheduled(initialDelay = 5000, fixedDelay = 20000)
	public void readTwitterFeed() throws TwitterException {
		Room myRoom = App.find("/twitter").room("twitter");

		if (myRoom.size() > 0) {
			Twitter twitter = TwitterFactory.getSingleton();
			Query query = new Query("java");
			query.setCount(20);
			query.setSinceId(sinceId);

			QueryResult result = twitter.search(query);
			sinceId = result.getMaxId();
			List<Status> statuses = result.getTweets();

			List<Tweet> tweets = Lists.newArrayList();
			for (Status status : statuses) {
				Tweet tweet = new Tweet();
				tweet.setCreatedAt(status.getCreatedAt().getTime());
				tweet.setFromUser(status.getUser().getName());
				tweet.setId(status.getId());
				tweet.setProfileImageUrl(status.getUser().getProfileImageURL());
				tweet.setText(status.getText());
				tweets.add(tweet);
			}

			if (!tweets.isEmpty()) {
				myRoom.send("newTweets", tweets);
				myRoom.set(LAST_RECEIVED_TWEETS_KEY, tweets);
			}
		}
	}

}
