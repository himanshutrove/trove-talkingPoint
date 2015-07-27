package trove.talkingPoint;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class EntryServlet {
	private static Date cachedTime = null;
	private static JSONObject cache = new JSONObject();
	private static Processor processor = new Processor();

	public JSONObject getCache() {
		if (cachedTime == null) {
			cache = processor.process();
			cachedTime = new Date();
		} else if (cachedTime.getTime() - new Date().getTime() >= 2000000) {
			cache = processor.process();
			cachedTime = new Date();
		} else if (cache == null) {
			cache = processor.process();
		}
		return cache;
	}

	public Map<String, List<String>> getTrends() {
		if (cachedTime == null) {
			cache = processor.process();
			cachedTime = new Date();
			return processor.getTrendsMap();
		} else if (cachedTime.getTime() - new Date().getTime() >= 2000000) {
			processor.process();
			cachedTime = new Date();
			return processor.getTrendsMap();
		} else if (cache == null) {
			processor.process();
			return processor.getTrendsMap();
		}
		return processor.getTrendsMap();
	}
}
