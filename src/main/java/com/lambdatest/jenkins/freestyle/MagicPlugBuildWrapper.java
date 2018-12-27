package com.lambdatest.jenkins.freestyle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.CheckForNull;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdatest.jenkins.credential.MagicPlugCredentials;
import com.lambdatest.jenkins.credential.MagicPlugCredentialsImpl;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.service.CapabilityService;
import com.lambdatest.jenkins.freestyle.data.LocalTunnel;
import com.lambdatest.jenkins.freestyle.service.LambdaTunnelService;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.ItemGroup;
import hudson.tasks.BuildWrapper;
import hudson.util.Secret;
import net.sf.json.JSONObject;

@SuppressWarnings("serial")
public class MagicPlugBuildWrapper extends BuildWrapper implements Serializable {

	private List<JSONObject> seleniumCapabilityRequest;
	private String credentialsId;
	private String username;
	private Secret accessToken;
	private String gridURL;
	private String choice;
	private LocalTunnel localTunnel;
	private boolean useLocalTunnel;
	private String tunnelName;
	private Process tunnelProcess;
	private final static Logger logger = Logger.getLogger(MagicPlugBuildWrapper.class.getName());

	@DataBoundConstructor
	public MagicPlugBuildWrapper(StaplerRequest req, @CheckForNull List<JSONObject> seleniumCapabilityRequest,
			@CheckForNull String credentialsId, String choice, boolean useLocalTunnel, LocalTunnel localTunnel,
			ItemGroup context) throws Exception {
		try {
			choice = "beta";
			System.out.println(credentialsId);
			System.out.println(localTunnel);
			System.out.println(useLocalTunnel);
			if (seleniumCapabilityRequest == null) {
				// prevent null pointer
				this.seleniumCapabilityRequest = new ArrayList<JSONObject>();
			} else {
				System.out.println(seleniumCapabilityRequest);
				validateTestInput(seleniumCapabilityRequest);
				this.seleniumCapabilityRequest = seleniumCapabilityRequest;
				this.choice = choice;
				setCredentials(credentialsId, context);
				setCredentialsId(credentialsId);
			}
			if (localTunnel != null) {
				this.localTunnel = localTunnel;
				this.useLocalTunnel = true;
				this.tunnelName = localTunnel.getTunnelName();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void configureTunnel(LocalTunnel localTunnel, String buildname, String buildnumber) {
		System.out.println("Tunnel Config:" + localTunnel);
		if (StringUtils.isBlank(localTunnel.getTunnelName())) {
			localTunnel.setTunnelName(Constant.DEFAULT_TUNNEL_NAME);
		}
		StringBuilder sb = new StringBuilder(localTunnel.getTunnelName()).append("-").append(buildname).append("-")
				.append(buildnumber);
		String tunnelNameExt = sb.toString();
		localTunnel.setTunnelName(tunnelNameExt);
		this.localTunnel.setTunnelName(tunnelNameExt);
		this.tunnelProcess = LambdaTunnelService.setUp(this.username, this.accessToken.getPlainText(),
				localTunnel.getTunnelName());
	}

	private void validateTestInput(List<JSONObject> seleniumCapabilityRequest) {
		// TODO : Validation For Input Data
	}

	private void setCredentials(String credentialsId, ItemGroup context) throws Exception {
		final MagicPlugCredentials magicPlugCredential = MagicPlugCredentialsImpl.getCredentials(credentialsId,
				context);
		if (magicPlugCredential != null) {
			this.username = magicPlugCredential.getUserName();
			this.accessToken = magicPlugCredential.getAccessToken();
			System.out.println(username + ":" + accessToken.getPlainText());
			// Verify Auth Token Once again
			if (CapabilityService.isValidUser(this.username, this.accessToken.getPlainText())) {
				System.out.println("Valid User");
			} else {
				throw new Exception("Invalid Credentials ...");
			}
		} else {
			throw new Exception("Credentials not found");
		}
	}

	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener)
			throws IOException, InterruptedException {
		System.out.println("Environment setUp() -start");

		String buildname = build.getFullDisplayName().substring(0,
				build.getFullDisplayName().length() - (String.valueOf(build.getNumber()).length() + 1));
		String buildnumber = String.valueOf(build.getNumber());
		System.out.println("buildname :" + buildname);
		System.out.println("buildnumber :" + buildnumber);
		// Configure Tunnel
		if (this.localTunnel != null) {
			configureTunnel(this.localTunnel, buildname, buildnumber);
		}
		for (JSONObject seleniumCapabilityRequest : seleniumCapabilityRequest) {
			createFreeStyleBuildActions(build, buildname, buildnumber, seleniumCapabilityRequest, this.username,
					this.accessToken.getPlainText(), this.choice);
		}
		System.out.println("Adding LT actions done");

		// Create Grid URL
		this.gridURL = CapabilityService.buildHubURL(this.username, this.accessToken.getPlainText(), this.choice);
		System.out.println(this.gridURL);
		System.out.println("Environment setUp() -end");
		return new MagicPlugEnvironment(build);
	}

	public static void createFreeStyleBuildActions(AbstractBuild build, String buildname, String buildnumber,
			JSONObject seleniumCapabilityRequest, String username, String accessToken, String choice) {
		String operatingSystem = seleniumCapabilityRequest.getString(Constant.OPERATING_SYSTEM);
		String browserName = seleniumCapabilityRequest.getString(Constant.BROWSER_NAME);
		String browserVersion = seleniumCapabilityRequest.getString(Constant.BROWSER_VERSION);
		String resolution = seleniumCapabilityRequest.getString(Constant.RESOLUTION);

		LambdaFreeStyleBuildAction lfsBuildAction = new LambdaFreeStyleBuildAction("SeleniumTest", operatingSystem,
				browserName, browserVersion, resolution);
		lfsBuildAction.setBuild(build);
		lfsBuildAction.setBuildName(buildname);
		lfsBuildAction.setBuildNumber(buildnumber);
		lfsBuildAction.setIframeLink(CapabilityService.buildIFrameLink(buildnumber, username, accessToken, choice));
		build.addAction(lfsBuildAction);
	}

	@Override
	public MagicPlugDescriptor getDescriptor() {
		return (MagicPlugDescriptor) super.getDescriptor();
	}

	@SuppressWarnings("rawtypes")
	public class MagicPlugEnvironment extends BuildWrapper.Environment {
		private AbstractBuild build;

		private MagicPlugEnvironment(final AbstractBuild build) {
			this.build = build;
		}

		@Override
		public void buildEnvVars(Map<String, String> env) {
			String buildname = build.getFullDisplayName().substring(0,
					build.getFullDisplayName().length() - (String.valueOf(build.getNumber()).length() + 1));
			String buildnumber = String.valueOf(build.getNumber());
			if (!CollectionUtils.isEmpty(seleniumCapabilityRequest) && seleniumCapabilityRequest.size() == 1) {
				JSONObject seleniumCapability = seleniumCapabilityRequest.get(0);
				env.put(Constant.LT_OPERATING_SYSTEM, seleniumCapability.getString(Constant.OPERATING_SYSTEM));
				env.put(Constant.LT_BROWSER_NAME, seleniumCapability.getString(Constant.BROWSER_NAME));
				env.put(Constant.LT_BROWSER_VERSION, seleniumCapability.getString(Constant.BROWSER_VERSION));
				env.put(Constant.LT_RESOLUTION, seleniumCapability.getString(Constant.RESOLUTION));
			}
			env.put(Constant.LT_BROWSERS, createBrowserJSON(seleniumCapabilityRequest));
			env.put(Constant.LT_GRID_URL, gridURL);
			env.put(Constant.LT_BUILD_NAME, buildname);
			env.put(Constant.LT_BUILD_NUMBER, buildnumber);
			if (localTunnel != null) {
				env.put(Constant.LT_TUNNEL_NAME, localTunnel.getTunnelName());
			}
			env.put(Constant.USERNAME, username);
			System.out.println(env);
			super.buildEnvVars(env);
		}

		private String createBrowserJSON(List<JSONObject> seleniumCapabilityRequests) {
			String config = Constant.NOT_AVAILABLE;
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				config = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(seleniumCapabilityRequests);
			} catch (JsonProcessingException e) {
				logger.warning(e.getMessage());
			}
			return config;
		}

		@Override
		public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
			/*
			 * Runs after the build
			 */
			try {
				logger.info("tearDown");
				if (tunnelProcess != null && tunnelProcess.isAlive()) {
					logger.info("tunnel is active, going to stop tunnel binary");
					tunnelProcess.destroyForcibly();
					logger.info("Tunnel destroyed");
				}
			} catch (Exception e) {
				logger.warning(e.getMessage());
			}
			return super.tearDown(build, listener);
		}

	}

	public List<JSONObject> getSeleniumCapabilityRequest() {
		return seleniumCapabilityRequest;
	}

	public void setSeleniumCapabilityRequest(List<JSONObject> seleniumCapabilityRequest) {
		this.seleniumCapabilityRequest = seleniumCapabilityRequest;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Secret getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(Secret accessToken) {
		this.accessToken = accessToken;
	}

	public String getGridURL() {
		return gridURL;
	}

	public void setGridURL(String gridURL) {
		this.gridURL = gridURL;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String getCredentialsId() {
		return credentialsId;
	}

	public void setCredentialsId(String credentialsId) {
		this.credentialsId = credentialsId;
	}

	public Process getTunnelProcess() {
		return tunnelProcess;
	}

	public void setTunnelProcess(Process tunnelProcess) {
		this.tunnelProcess = tunnelProcess;
	}

	public LocalTunnel getLocalTunnel() {
		return localTunnel;
	}

	public void setLocalTunnel(LocalTunnel localTunnel) {
		this.localTunnel = localTunnel;
	}

	public boolean isUseLocalTunnel() {
		return useLocalTunnel;
	}

	public void setUseLocalTunnel(boolean useLocalTunnel) {
		this.useLocalTunnel = useLocalTunnel;
	}

	public String getTunnelName() {
		return tunnelName;
	}

	public void setTunnelName(String tunnelName) {
		this.tunnelName = tunnelName;
	}

}
