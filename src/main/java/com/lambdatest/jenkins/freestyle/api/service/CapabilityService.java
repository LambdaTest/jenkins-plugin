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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.auth.UserAuthResponse;
import com.lambdatest.jenkins.freestyle.api.browser.BrowserList;
import com.lambdatest.jenkins.freestyle.api.osystem.OSList;

public class CapabilityService {

	public static Map<String, String> supportedOS;
	public static List<String> supportedBrowsers;
	public static Map<String, String> supportedResolutions;

	public static Map<String, String> getOperatingSystems() {
		try {
			if (MapUtils.isEmpty(supportedOS)) {
				supportedOS = new HashMap<>();
				supportedResolutions = new HashMap<>();
				String url = "https://dev-ml.lambdatest.com/api/v1/capability";

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
				String jsonResponse = result.toString();
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				// convert json string to object
				OSList osList = objectMapper.readValue(jsonResponse, OSList.class);
				convertSupportedOSToList(osList);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CapabilityService.supportedOS;
	}

	private static void convertSupportedOSToList(OSList osList) {
		if (osList.getOs() != null) {
			osList.getOs().forEach(os -> {
				CapabilityService.supportedOS.put(os.getId(), os.getName());
				CapabilityService.supportedResolutions.put(os.getId(), os.getResolution());
			});
		}
	}

	public static List<String> getBrowsers(String operatingSystem) {
		supportedBrowsers = new ArrayList<String>();
		try {
			String url = "https://dev-ml.lambdatest.com/api/v1/capability?os=" + operatingSystem;

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);

			// add request header
			request.addHeader("Content-Type", "application/json");
			HttpResponse response;

			response = client.execute(request);

			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			String jsonResponse = result.toString();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			// convert json string to object
			BrowserList bsList = objectMapper.readValue(jsonResponse, BrowserList.class);
			supportedBrowsers = convertObjectToList(supportedBrowsers, bsList);
			System.out.println(supportedBrowsers);
		} catch (Exception e) {
			e.printStackTrace();
			if (CollectionUtils.isEmpty(supportedBrowsers)) {
				supportedBrowsers.add(Constant.NOT_AVAILABLE);
			}
		}

		return supportedBrowsers;

	}

	private static List<String> convertObjectToList(List<String> browsers, BrowserList bsList) {
		if (bsList != null) {
			if (bsList.getChrome() != null) {
				browsers.addAll(bsList.getChrome().getChromeBrowsersList());
			}
			if (bsList.getFirefox() != null) {
				browsers.addAll(bsList.getFirefox().getFirefoxBrowsersList());
			}
			if (bsList.getEdge() != null) {
				browsers.addAll(bsList.getEdge().getEdgeBrowsersList());
			}
			if (bsList.getIe() != null) {
				browsers.addAll(bsList.getIe().getIEBrowsersList());
			}
		} else {
			browsers.add(Constant.NOT_AVAILABLE);
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
			String url = "https://dev-accounts.lambdatest.com/api/user/token/auth";

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(url);

			// add header
			httpPost.addHeader("Accept", "application/json");
			httpPost.addHeader("Content-Type", "application/json");
			// List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			// params.add(new BasicNameValuePair("username", username));
			// params.add(new BasicNameValuePair("token", authKey));
			// httpPost.setEntity(new UrlEncodedFormEntity(params));

			String json = "{\"username\":\"" + username + "\",\"token\":\"" + authKey + "\"}";
			StringEntity entity = new StringEntity(json);
			httpPost.setEntity(entity);

			HttpResponse response = client.execute(httpPost);
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

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
		System.out.println(isValidUser("sushobhitd", "nao0GtdaCnq9GSc29ZTKQT90BrfzCUc8s9VRMVnMxf8WAtsDx3"));
		// getOperatingSystems();
		// System.out.println(supportedOS);
		// System.out.println(getResolution("win10"));
	}

}
