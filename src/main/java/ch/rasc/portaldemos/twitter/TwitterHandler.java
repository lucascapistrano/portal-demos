package ch.rasc.portaldemos.twitter;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.github.flowersinthesand.portal.Bean;
import com.github.flowersinthesand.portal.On;
import com.github.flowersinthesand.portal.Room;
import com.github.flowersinthesand.portal.Socket;
import com.github.flowersinthesand.portal.Wire;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MinMaxPriorityQueue;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;

@Bean
@Component
public class TwitterHandler {

	final static String LAST_RECEIVED_TWEETS_KEY = "LAST_RECEIVED_TWEETS";

	@Wire
	Room hall;

	@Autowired
	private Environment environment;

	private BasicClient client;

	private ExecutorService executorService;

	private Twitter4jStatusClient t4jClient;

	@On
	public void open(Socket socket) {
		Queue<Tweet> lastTweets = (Queue<Tweet>) hall.get(LAST_RECEIVED_TWEETS_KEY);
		if (lastTweets != null) {
			socket.send("newTweets", ImmutableList.copyOf(lastTweets));
		}
	}

	public void init() {
		MinMaxPriorityQueue<Tweet> lastTweets = MinMaxPriorityQueue.maximumSize(10).create();
		hall.set(LAST_RECEIVED_TWEETS_KEY, lastTweets);

		BlockingQueue<String> queue = new LinkedBlockingQueue<>(100);
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

		endpoint.trackTerms(ImmutableList.of("ExtJS", "Sencha", "atmo_framework", "#java", "java7", "java8",
				"websocket", "#portal", "html5", "javascript"));
		endpoint.languages(ImmutableList.of("en", "de"));

		String consumerKey = environment.getProperty("twitter4j.oauth.consumerKey");
		String consumerSecret = environment.getProperty("twitter4j.oauth.consumerSecret");
		String accessToken = environment.getProperty("twitter4j.oauth.accessToken");
		String accessTokenSecret = environment.getProperty("twitter4j.oauth.accessTokenSecret");

		Authentication auth = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);

		client = new ClientBuilder().hosts(Constants.STREAM_HOST).endpoint(endpoint).authentication(auth)
				.processor(new StringDelimitedProcessor(queue)).build();

		executorService = Executors.newSingleThreadExecutor();

		TwitterStatusListener statusListener = new TwitterStatusListener(hall);
		t4jClient = new Twitter4jStatusClient(client, queue, ImmutableList.of(statusListener), executorService);

		t4jClient.connect();
		t4jClient.process();
	}

	@PreDestroy
	public void destroy() {
		t4jClient.stop();
		client.stop();
		executorService.shutdown();
	}

}
