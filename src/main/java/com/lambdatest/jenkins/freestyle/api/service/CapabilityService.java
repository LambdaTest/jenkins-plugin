package com.lambdatest.jenkins.freestyle.api.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpResponse;
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
import com.lambdatest.jenkins.freestyle.api.osystem.Version;
import com.lambdatest.jenkins.freestyle.api.osystem.OSList;

public class CapabilityService {

	public static Map<String, String> supportedOS = new HashMap<>();
	public static List<String> supportedBrowsers;
	public static Map<String, String> supportedResolutions = new HashMap<>();
	public static Map<String, Version> allBrowserData = new HashMap<>();

	public static Map<String, String> getOperatingSystems() {
		try {
			if (MapUtils.isEmpty(supportedOS)) {
				supportedOS = new HashMap<>();
				supportedResolutions = new HashMap<>();

				HttpClient client = HttpClientBuilder.create().build();
				HttpGet request = new HttpGet(Constant.OS_API_URL);

				// add request header
				request.addHeader("Content-Type", "application/json");
				HttpResponse response = client.execute(request);

				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				String jsonResponse = result.toString();
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
			if (osList.getBrowsers() != null) {
				osList.getBrowsers().forEach(br -> {
					br.getVersions().forEach(version -> {
						allBrowserData.put(version.getId(), version);
					});
				});
			}
		}
	}

	public static List<String> getBrowsers(String operatingSystem) {
		supportedBrowsers = new ArrayList<String>();
		try {
			String browserApiURL = Constant.BROWSER_API_URL + operatingSystem;

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(browserApiURL);

			// add request header
			request.addHeader("Content-Type", "application/json");
			HttpResponse response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			String jsonResponse = result.toString();

			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<Browser> browsers = objectMapper.readValue(jsonResponse, new TypeReference<List<Browser>>() {
			});
			supportedBrowsers = parseSupportedBrowsers(supportedBrowsers, browsers);
		} catch (Exception e) {
			e.printStackTrace();
			if (CollectionUtils.isEmpty(supportedBrowsers)) {
				supportedBrowsers.add(Constant.NOT_AVAILABLE);
			}
		}

		return supportedBrowsers;

	}

	private static List<String> parseSupportedBrowsers(List<String> browsers, List<Browser> browser) {

		if (!CollectionUtils.isEmpty(browser)) {
			browser.forEach(br -> {
				if (!CollectionUtils.isEmpty(br.getVersions())) {
					br.getVersions().forEach(version -> {
						browsers.add(version.getId());
					});
				}
			});
		}
		return browsers;
	}

	public static List<String> getResolution(String operatingSystem) {
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

	public static void main(String[] args) throws Exception {
		// System.out.println(isValidUser("sushobhitd",
		// "nao0GtdaCnq9GSc29ZTKQT90BrfzCUc8s9VRMVnMxf8WAtsDx3"));
		System.out.println(getOperatingSystems());
		System.out.println(allBrowserData);
		
		// System.out.println(supportedOS);
		// System.out.println(getResolution("win10"));
		// getBrowsers("win10");
	}

}
