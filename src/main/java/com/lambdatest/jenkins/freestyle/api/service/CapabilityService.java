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
import com.lambdatest.jenkins.freestyle.api.auth.UserAuthResponse;
import com.lambdatest.jenkins.freestyle.api.browser.Browser;
import com.lambdatest.jenkins.freestyle.api.browser.BrowserVersion;
import com.lambdatest.jenkins.freestyle.api.osystem.OSList;

public class CapabilityService {

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
			e.printStackTrace();
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
				System.out.println("Supported Browser List Exists for " + operatingSystem);
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
			e.printStackTrace();
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
		System.out.println("\n*** START ***");
		//System.out.println(allBrowserVersions.keySet().toString());
		System.out.println("\n*** END ***");
		VersionKey vk = new VersionKey(operatingSystem, browserName);
		if (allBrowserVersions.containsKey(vk)) {
			allBrowserVersions.get(vk).forEach(bv -> {
				supportedBrowserVersions.add(bv.getVersion());
			});
		} else {
			System.out.println(vk + " not found");
			System.out.println("\n*** NOT FOUND ***");
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

	public static boolean isValidUser(String username, String authKey) {
		boolean validUser = false;
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(Constant.AUTH_API_URL);

			// add header
			httpPost.addHeader("Accept", "application/json");
			httpPost.addHeader("Content-Type", "application/json");
			String json = "{\"username\":\"" + username + "\",\"token\":\"" + authKey + "\"}";
			StringEntity entity = new StringEntity(json);
			httpPost.setEntity(entity);

			HttpResponse response = client.execute(httpPost);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			String jsonResponse = result.toString();
			System.out.println(jsonResponse);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			// convert json string to object
			UserAuthResponse userAuthResponse = objectMapper.readValue(jsonResponse, UserAuthResponse.class);
			if (userAuthResponse != null && userAuthResponse.getUsername() != null) {
				validUser = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return validUser;
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
			e.printStackTrace();
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
		request.addHeader("Content-Type", "application/json");
		HttpResponse response = client.execute(request);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	public static void main(String[] args) throws Exception {
		// System.out.println(isValidUser("sushobhitd",
		// "nao0GtdaCnq9GSc29ZTKQT90BrfzCUc8s9VRMVnMxf8WAtsDx3"));
		System.out.println(getOperatingSystems());
		Set brs = getBrowserNames("win10");
		System.out.println(brs);
		System.out.println(getBrowserVersions("win10", "chrome"));
		System.out.println(allBrowserVersions);
		// List<String> s = getBrowserVersions("win10", "firefox");
		// System.out.println(s);

		// System.out.println(supportedOS);
		// System.out.println(getResolution("win10"));
		// getBrowsers("win10");
	}

}
