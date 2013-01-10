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
import com.google.common.collect.Lists;

@Service
public class TwitterReader {

	private long sinceId = 0;

	@Scheduled(initialDelay = 5000, fixedDelay = 20000)
	public void readTwitterFeed() throws TwitterException {

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
		App.find("/twitter").room("twitter").send("newTweets", tweets);
	}

}
