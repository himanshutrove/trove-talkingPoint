package trove.talkingPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestResponder {

	@RequestMapping(method = RequestMethod.GET, value = "/url")
	public String getURLS(
			@RequestParam(value = "region", defaultValue = "World") String name) {
		EntryServlet entryServlet = new EntryServlet();
		return entryServlet.getCache().toString();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/url/region/{region}")
	public String getRegion(@PathVariable("region") String region,
			@RequestParam(value = "type", required = false) String type) {
		EntryServlet entryServlet = new EntryServlet();
		JSONObject jsonObject = entryServlet.getCache();
		JSONObject result = null;
		if (StringUtils.isNotBlank(region)) {
			result = jsonObject.getJSONObject(region);
		}
		if (StringUtils.isNotBlank(type)) {
			return result.get(type).toString();
		}
		return result.toString();

	}

	@RequestMapping(method = RequestMethod.GET, value = "/trends")
	public Map<String, List<String>> getTrends(
			@RequestParam(value = "region", required = false) String region) {
		EntryServlet entryServlet = new EntryServlet();
		if (StringUtils.isNotBlank(region)) {
			Map<String, List<String>> map = entryServlet.getTrends();
			if (map.get(region) != null && !map.get(region).isEmpty()) {
				Map<String, List<String>> retrunMap = new HashMap<String, List<String>>();
				retrunMap.put(region, map.get(region));
				return retrunMap;
			} else {
				return null;
			}
		}
		return entryServlet.getTrends();

	}
}
