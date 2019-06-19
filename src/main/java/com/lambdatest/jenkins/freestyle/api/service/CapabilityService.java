package com.lambdatest.jenkins.freestyle.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.auth.UserAuthRequest;
import com.lambdatest.jenkins.freestyle.api.auth.UserAuthResponse;
import com.lambdatest.jenkins.freestyle.api.browser.Browser;
import com.lambdatest.jenkins.freestyle.api.browser.BrowserVersion;
import com.lambdatest.jenkins.freestyle.api.osystem.OSList;

public class CapabilityService {

	private final static Logger logger = Logger.getLogger(CapabilityService.class.getName());

	public static Map<String, String> supportedOS = new LinkedHashMap<>();
	public static Set<String> supportedBrowsers;
	public static Map<String, Set<String>> allBrowserNames = new LinkedHashMap<>();
	public static Map<VersionKey, List<BrowserVersion>> allBrowserVersions = new LinkedHashMap<>();

	public static Set<String> supportedBrowserVersions;
	public static Map<String, String> supportedResolutions = new LinkedHashMap<>();

	public static Map<String, String> getOperatingSystems() {
		try {
			if (MapUtils.isEmpty(supportedOS)) {
				supportedOS = new LinkedHashMap<>();
				supportedResolutions = new LinkedHashMap<>();
				String jsonResponse = sendGetRequest(Constant.OS_API_URL);
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				OSList osList = objectMapper.readValue(jsonResponse, OSList.class);
				parseSupportedOsAndResolution(osList);
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		return CapabilityService.supportedOS;
	}

	private static void parseSupportedOsAndResolution(OSList osList) {
		if (osList != null && osList.getOs() != null) {
			osList.getOs().forEach(os -> {
				CapabilityService.supportedOS.put(os.getId(), os.getName());
				CapabilityService.supportedResolutions.put(os.getId(), os.getResolution());
			});
		}

	}

	public static Set<String> getBrowserNames(String operatingSystem) {
		try {
			if (allBrowserNames.containsKey(operatingSystem)) {
				logger.info("Supported Browser List Exists for " + operatingSystem);
				return allBrowserNames.get(operatingSystem);
			}
			supportedBrowsers = new LinkedHashSet<String>();
			String browserApiURL = Constant.BROWSER_API_URL + operatingSystem;
			String jsonResponse = sendGetRequest(browserApiURL);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<Browser> browsers = objectMapper.readValue(jsonResponse, new TypeReference<List<Browser>>() {
			});
			parseSupportedBrowsers(browsers, operatingSystem);
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		return supportedBrowsers;
	}

	private static Set<String> parseSupportedBrowsers(List<Browser> browser, String operatingSystem) {
		if (!CollectionUtils.isEmpty(browser)) {
			browser.forEach(br -> {
				supportedBrowsers.add(br.getName());
				VersionKey vk = new VersionKey(operatingSystem, br.getName());
				if (!CollectionUtils.isEmpty(br.getVersions())) {
					allBrowserVersions.put(vk, br.getVersions());
				}
			});
			allBrowserNames.put(operatingSystem, supportedBrowsers);
		}
		return supportedBrowsers;
	}

	public static Set<String> getBrowserVersions(String operatingSystem, String browserName) {
		supportedBrowserVersions = new LinkedHashSet<String>();
		VersionKey vk = new VersionKey(operatingSystem, browserName);
		if (allBrowserVersions.containsKey(vk)) {
			allBrowserVersions.get(vk).forEach(bv -> {
				supportedBrowserVersions.add(bv.getVersion());
			});
		} else {
			//System.out.println(vk + " not found");
		}
		return supportedBrowserVersions;
	}

	public static List<String> getResolutions(String operatingSystem) {
		List<String> resolutions = new ArrayList<String>();
		if (!MapUtils.isEmpty(supportedResolutions) && supportedResolutions.containsKey(operatingSystem)) {
			String compactResolution = supportedResolutions.get(operatingSystem);
			resolutions = Arrays.asList(compactResolution.split(","));
		} else {
			resolutions.add(Constant.NOT_AVAILABLE);
		}
		return resolutions;

	}

	public static boolean isValidUser(String username, String token) {
		boolean validUser = false;
		try {
			UserAuthRequest request = new UserAuthRequest(username, token);
			String jsonResponse = sendPostRequest(Constant.AUTH_API_URL, request);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			// convert json string to object
			UserAuthResponse userAuthResponse = objectMapper.readValue(jsonResponse, UserAuthResponse.class);
			if (userAuthResponse != null && userAuthResponse.getUsername() != null) {
				validUser = true;
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		return validUser;
	}

	public static UserAuthResponse getUserInfo(String username, String token) {
		UserAuthResponse userAuthResponse = null;
		try {
			UserAuthRequest request = new UserAuthRequest(username, token);
			String jsonResponse = sendPostRequest(Constant.AUTH_API_URL, request);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			// convert json string to object
			userAuthResponse = objectMapper.readValue(jsonResponse, UserAuthResponse.class);
			if (userAuthResponse != null && userAuthResponse.getUsername() != null) {
				return userAuthResponse;
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		return userAuthResponse;
	}

	public boolean ping() {
		try {
			String jsonResponse = sendGetRequest(Constant.OS_API_URL);
			if (StringUtils.isEmpty(jsonResponse)) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
			return false;
		}
	}

	public static String buildIFrameLink(String buildNumber, String username, String accessToken, String type) {
		try {
			StringBuilder sb = new StringBuilder();
			if (Constant.STAGE.equals(type)) {
				sb.append(Constant.Stage.APP_URL);
			} else if (Constant.DEV.equals(type)) {
				sb.append(Constant.Dev.APP_URL);
			} else if (Constant.BETA.equals(type)) {
				sb.append(Constant.Beta.APP_URL);
			} else {
				sb.append(Constant.APP_URL);
			}
			sb.append("/jenkins/?buildID=[\"").append(buildNumber).append("\"]&token=").append(accessToken)
					.append("&username=").append(username).append("&auth=jenkins");
			return sb.toString();
		} catch (Exception e) {
			return Constant.NOT_AVAILABLE;
		}
	}

	public static String buildHubURL(String username, String accessToken, String type) {
		try {
			StringBuilder sb = new StringBuilder("https://");
			sb.append(username).append(":").append(accessToken);
			if (Constant.STAGE.equals(type)) {
				sb.append(Constant.Stage.HUB_URL);
			} else if (Constant.DEV.equals(type)) {
				sb.append(Constant.Dev.HUB_URL);
			} else if (Constant.BETA.equals(type)) {
				sb.append(Constant.Beta.HUB_URL);
			} else {
				sb.append(Constant.HUB_URL);
			}
			return sb.toString();
		} catch (Exception e) {
			return Constant.NOT_AVAILABLE;
		}
	}

	public static String sendGetRequest(String url) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		// add request header
		// request.addHeader("Content-Type", "application/json");
		HttpResponse response = client.execute(request);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	public static String sendPostRequest(String url, Object request) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(url);

		// add header
		httpPost.addHeader("Accept", "application/json");
		httpPost.addHeader("Content-Type", "application/json");
		// Creating Object of ObjectMapper define in Jakson Api
		String jsonStr = null;
		try {
			ObjectMapper Obj = new ObjectMapper();
			jsonStr = Obj.writeValueAsString(request);
			logger.info(jsonStr);
			StringEntity entity = new StringEntity(jsonStr);
			httpPost.setEntity(entity);

			HttpResponse response = client.execute(httpPost);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			String jsonResponse = result.toString();
			return jsonResponse;
		} catch (IOException e) {
			logger.warning(e.getMessage());
			return "";
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getOperatingSystems());
		System.out.println(getBrowserNames("win10"));
		System.out.println(getBrowserVersions("win10", "chrome"));
		System.out.println(allBrowserVersions);
	}

}
