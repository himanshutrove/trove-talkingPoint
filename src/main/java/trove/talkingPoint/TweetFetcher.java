package trove.talkingPoint;

import java.util.HashSet;
import java.util.Set;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetFetcher {
	/**
	 * @author himanshusharma
	 */
	private static TweetFetcher fetcher = new TweetFetcher();
	private static Twitter twitter = null;

	public static TweetFetcher getInstance() {
		return fetcher;

	}

	private TweetFetcher() {

	}

	public static Set<String> getTrends(Integer place) {
		Set<String> trendsList = new HashSet<String>();
		try {
			Trends t = twitter.getPlaceTrends(place);
			if (t != null && t.getTrends() != null) {
				Trend[] trend = t.getTrends();
				for (Trend tt : trend) {
					trendsList.add(tt.getQuery());
				}
			}

		} catch (TwitterException e) {
			System.out.println(e.getMessage());
		}

		return trendsList;
	}

	public static QueryResult getTweetsForHashTag(Query query) {
		QueryResult result = null;
		try {
			result = twitter.search(query);
		} catch (TwitterException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return result;
	}

	public static void configure() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("bv3KCtOzqHkByCrAcA42gg70L");
		cb.setOAuthConsumerSecret("o0PMOMmcNahgONOrDVejtwmHVZJJK2KJpm9V3HEXntQDARvp92");
		cb.setOAuthAccessToken("142042282-jpVEdr37JbHsM0U83a3MoataS846wE6yTtSYJJgI");
		cb.setOAuthAccessTokenSecret("6FGNGDPDCx4N6LUadMqcV6S2MnxxINnoaXqFoLWfkNdy4");

		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

}
