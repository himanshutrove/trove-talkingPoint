package trove.talkingPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.URLEntity;

/**
 * @author himanshusharma
 */
public class Processor {
	private static final String MEDIA = "media";
	private static final String ARTICLE = "article";
	private Map<String, Integer> locationmap = new HashMap<String, Integer>();
	private Pattern p = Pattern.compile("%22|%23", Pattern.CASE_INSENSITIVE);
	private String MOSTRETWEETED = "most_fav_tweet";
	private String MOSTFAV = "most_retweeted";
	private Map<String, List<String>> trendsMap = new HashMap<String, List<String>>();

	/**
	 * @return the trendsMap
	 */
	public Map<String, List<String>> getTrendsMap() {
		return trendsMap;
	}

	public Processor() {
		TweetFetcher.getInstance();
		TweetFetcher.configure();

	}

	private List<String> findMaxOccurance(Map<String, Integer> map, int n) {
		List<MaxOccurenceFinder> l = new ArrayList<MaxOccurenceFinder>();
		for (Map.Entry<String, Integer> entry : map.entrySet())
			l.add(new MaxOccurenceFinder(entry.getKey(), entry.getValue()));

		Collections.sort(l);
		List<String> list = new ArrayList<String>();
		if (l.size() > n) {
			for (MaxOccurenceFinder w : l.subList(0, n))
				list.add(w.urls + ":" + w.numberOfOccurrence);
		} else if (l.size() >= 2) {
			for (MaxOccurenceFinder w : l.subList(0, l.size() - 1))
				list.add(w.urls + ":" + w.numberOfOccurrence);

		} else if (l.size() >= 1) {
			list.add(l.get(0).urls + ":" + l.get(0).numberOfOccurrence);
		}
		return list;
	}

	private Map<String, List<String>> fetchMaxTweetedUrl(List<Status> statusList) {
		Map<String, Integer> urlMap = new HashMap<String, Integer>();
		Map<String, Integer> mediaUrlMap = new HashMap<String, Integer>();
		List<String> articleUrlList = new ArrayList<String>();
		List<String> mediaUrlList = new ArrayList<String>();
		String fTweetText = null;
		String rTweetText = null;
		if (statusList != null && !statusList.isEmpty()) {
			int rtweetedCount = 0;
			int fTweetCount = 0;

			for (Status status : statusList) {
				if (status.getRetweetCount() > rtweetedCount) {
					rTweetText = status.getText();
				}
				if (status.getFavoriteCount() > fTweetCount) {
					fTweetText = status.getText();
				}
				URLEntity[] entityUrl = null;
				MediaEntity[] mediaUrls = null;
				if (status.getURLEntities() != null) {
					entityUrl = status.getURLEntities();
					for (URLEntity url : entityUrl) {
						Integer occurance = urlMap.get(url.getExpandedURL()
								.toString());
						if (occurance != null) {
							urlMap.put(url.getExpandedURL().toString(),
									++occurance);
						} else {
							urlMap.put(url.getExpandedURL().toString(), 1);
						}
					}
				}
				if (status.getMediaEntities() != null) {
					mediaUrls = status.getMediaEntities();
					for (MediaEntity url : mediaUrls) {
						Integer occurance = mediaUrlMap.get(url
								.getExpandedURL().toString());
						if (occurance != null) {
							mediaUrlMap.put(url.getExpandedURL().toString(),
									++occurance);
						} else {
							mediaUrlMap.put(url.getExpandedURL().toString(), 1);
						}
					}
				}

			}
		}
		Map<String, List<String>> responseMap = new HashMap<String, List<String>>();
		if (urlMap != null) {
			articleUrlList = findMaxOccurance(urlMap, 10);
			responseMap.put(ARTICLE, articleUrlList);
		}
		if (mediaUrlMap != null && !mediaUrlMap.isEmpty()) {
			mediaUrlList = findMaxOccurance(mediaUrlMap, 10);
			responseMap.put(MEDIA, mediaUrlList);
		}
		if (StringUtils.isNotBlank(fTweetText)) {
			List<String> list = new ArrayList<String>();
			list.add(fTweetText);
			responseMap.put(MOSTFAV, list);
		}
		if (StringUtils.isNotBlank(rTweetText)) {
			List<String> list = new ArrayList<String>();
			list.add(rTweetText);
			responseMap.put(MOSTRETWEETED, list);
		}

		return responseMap;
	}

	private void updateMap() {
		locationmap.put("South Africa", 23424942);
		locationmap.put("India", 23424848);
		locationmap.put("USA", 23424977);
		locationmap.put("UK", 23424975);
		locationmap.put("canada", 23424775);

	}

	public JSONObject process() {
		if (locationmap.isEmpty()) {
			updateMap();
		}
		JSONObject jsonObject = new JSONObject();

		for (Entry<String, Integer> en : locationmap.entrySet()) {
			List<String> trendList = new ArrayList<String>();
			trendsMap.put(en.getKey(), trendList);
			JSONObject countryObject = new JSONObject();
			jsonObject.put(en.getKey(), countryObject);
			Set<String> trends = TweetFetcher.getTrends(en.getValue());
			if (trends != null && !trends.isEmpty()) {
				for (String trend : trends) {
					Query query = new Query(trend + "+filter:links");
					QueryResult result = TweetFetcher
							.getTweetsForHashTag(query);
					Map<String, List<String>> urlMap = fetchMaxTweetedUrl(result
							.getTweets());
					JSONObject trendObject = getTrends(urlMap);
					Matcher m = p.matcher(trend);
					String key = m.replaceAll("");
					key = key.replace("+", "");
					trendList.add(key);
					countryObject.append(key, trendObject);

				}
			}
		}
		if (StringUtils.isNotBlank(MOSTRETWEETED)) {
			jsonObject.append("Most_ReTweeted", MOSTRETWEETED);
		}
		if (StringUtils.isNotBlank(MOSTFAV)) {
			jsonObject.append("most_fav", MOSTFAV);
		}

		return jsonObject;

	}

	private JSONObject getTrends(Map<String, List<String>> urlMap) {
		JSONObject jsonObject = new JSONObject();
		if (!urlMap.isEmpty() && urlMap.get(ARTICLE) != null) {
			JSONObject articleList = new JSONObject();
			jsonObject.append(ARTICLE, articleList);
			for (String url : urlMap.get(ARTICLE)) {
				articleList.append(url, ARTICLE);
			}
		}
		if (!urlMap.isEmpty() && urlMap.get(MEDIA) != null) {
			JSONObject articleList = new JSONObject();
			jsonObject.append(MEDIA, articleList);
			for (String url : urlMap.get(MEDIA)) {
				articleList.append(url, MEDIA);
			}
		}
		if (!urlMap.isEmpty() && urlMap.get(MOSTFAV) != null) {
			JSONObject articleList = new JSONObject();
			jsonObject.append(MOSTFAV, articleList);
			for (String url : urlMap.get(MOSTFAV)) {
				articleList.append(url, MOSTFAV);
			}
		}
		if (!urlMap.isEmpty() && urlMap.get(MOSTRETWEETED) != null) {
			JSONObject articleList = new JSONObject();
			jsonObject.append(MOSTRETWEETED, articleList);
			for (String url : urlMap.get(MOSTRETWEETED)) {
				articleList.append(url, MOSTRETWEETED);
			}
		}
		return jsonObject;
	}
}
