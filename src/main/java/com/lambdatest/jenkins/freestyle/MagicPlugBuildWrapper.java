package com.lambdatest.jenkins.freestyle;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

import javax.annotation.CheckForNull;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdatest.jenkins.analytics.AnalyticService;
import com.lambdatest.jenkins.analytics.data.AnalyticRequest;
import com.lambdatest.jenkins.credential.MagicPlugCredentials;
import com.lambdatest.jenkins.credential.MagicPlugCredentialsImpl;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.auth.UserAuthResponse;
import com.lambdatest.jenkins.freestyle.api.service.CapabilityService;
import com.lambdatest.jenkins.freestyle.data.LocalTunnel;
import com.lambdatest.jenkins.freestyle.service.LambdaTunnelService;

import hudson.FilePath;
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
	private UserAuthResponse userAuthResponse;

	private static final Logger logger = LogManager.getLogger(MagicPlugBuildWrapper.class);

	
	@DataBoundConstructor
	public MagicPlugBuildWrapper(StaplerRequest req, @CheckForNull List<JSONObject> seleniumCapabilityRequest,
			@CheckForNull String credentialsId, String choice, boolean useLocalTunnel, LocalTunnel localTunnel,
			ItemGroup context) throws Exception {
		try {
			choice = "prod";
			if (seleniumCapabilityRequest == null) {
				// prevent null pointer
				this.seleniumCapabilityRequest = new ArrayList<JSONObject>();
			} else {
				validateTestInput(seleniumCapabilityRequest);
				this.seleniumCapabilityRequest = seleniumCapabilityRequest;
			}
			// Setting up credentials in both case if input capabilities are there or not
			this.choice = choice;
			setLambdaTestCredentials(credentialsId, context);
			setCredentialsId(credentialsId);
			if (localTunnel != null) {
				logger.info(localTunnel.toString());
				this.localTunnel = localTunnel;
				this.useLocalTunnel = true;
				this.tunnelName = localTunnel.getTunnelName();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void configureTunnel(LocalTunnel localTunnel, String buildname, String buildnumber, FilePath workspacePath) {
		if (StringUtils.isBlank(localTunnel.getTunnelName())) {
			localTunnel.setTunnelName(Constant.DEFAULT_TUNNEL_NAME);
			this.localTunnel.setTunnelName(Constant.DEFAULT_TUNNEL_NAME);
		}
		String tunnelNameExt = getTunnelIdentifierExtended(localTunnel.getTunnelName(), buildname, buildnumber);
		this.tunnelProcess = LambdaTunnelService.setUp(this.username, this.accessToken.getPlainText(), tunnelNameExt,workspacePath);
	}

	private String getTunnelIdentifierExtended(String tunnelName, String buildname, String buildnumber) {
		StringBuilder sb = new StringBuilder(tunnelName.trim());
		// StringBuilder sb = new
		// StringBuilder(tunnelName.trim()).append("-").append(buildname.trim()).append("-")
		// .append(buildnumber);
		return sb.toString();
	}

	private void validateTestInput(List<JSONObject> seleniumCapabilityRequest) {
		// TODO : Validation For Input Data
	}

	private void setLambdaTestCredentials(String credentialsId, ItemGroup context) throws Exception {
		final MagicPlugCredentials magicPlugCredential = MagicPlugCredentialsImpl.getCredentials(credentialsId,
				context);
		if (magicPlugCredential != null) {
			this.username = magicPlugCredential.getUserName();
			this.accessToken = magicPlugCredential.getAccessToken();
			// Verify Auth Token Once again
			this.userAuthResponse = CapabilityService.getUserInfo(this.username, this.accessToken.getPlainText());
			if (this.userAuthResponse != null && !StringUtils.isEmpty(this.userAuthResponse.getUsername())) {
				logger.info("Valid User");
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
		logger.info(build.getWorkspace());
		String buildname = build.getFullDisplayName().substring(0,
				build.getFullDisplayName().length() - (String.valueOf(build.getNumber()).length() + 1));
		String buildnumber = String.valueOf(build.getNumber());
		// Configure Tunnel
		if (this.localTunnel != null) {
			configureTunnel(this.localTunnel, buildname, buildnumber,build.getWorkspace());
		}
		for (JSONObject seleniumCapabilityRequest : seleniumCapabilityRequest) {
			createFreeStyleBuildActions(build, buildname, buildnumber, seleniumCapabilityRequest, this.username,
					this.accessToken.getPlainText(), this.choice);
		}

		// Create Grid URL
		this.gridURL = CapabilityService.buildHubURL(this.username, this.accessToken.getPlainText(),"production");
		logger.info(this.gridURL);
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
				env.put(Constant.LT_PLATFORM, seleniumCapability.getString(Constant.OPERATING_SYSTEM));
				env.put(Constant.LT_BROWSER_NAME, seleniumCapability.getString(Constant.BROWSER_NAME));
				env.put(Constant.LT_BROWSER_VERSION, seleniumCapability.getString(Constant.BROWSER_VERSION));
				env.put(Constant.LT_RESOLUTION, seleniumCapability.getString(Constant.RESOLUTION));
			}
			env.put(Constant.LT_BROWSERS, createBrowserJSON(seleniumCapabilityRequest));
			env.put(Constant.LT_GRID_URL, gridURL);
			env.put(Constant.LT_BUILD_NAME, buildname);
			env.put(Constant.LT_BUILD_NUMBER, buildnumber);
			AnalyticRequest analyticRequest = null;
			if (userAuthResponse != null) {
				analyticRequest = new AnalyticRequest(userAuthResponse.getId(), gridURL, buildname, build.getNumber());
			} else {
				analyticRequest = new AnalyticRequest();
			}
			if (localTunnel != null) {
				String tunnelName = getTunnelIdentifierExtended(localTunnel.getTunnelName(), buildname, buildnumber);
				analyticRequest.setTunnelName(tunnelName);
				env.put(Constant.LT_TUNNEL_NAME, tunnelName);
			}
			env.put(Constant.LT_USERNAME, username);
			env.put(Constant.LT_ACCESS_KEY, accessToken.getPlainText());
			// For Plugin Analytics Service
			try {
				AnalyticService as = new AnalyticService();
				as.setAnalyticRequest(analyticRequest);
				new Thread(as).start();
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}
			super.buildEnvVars(env);
		}

		private String createBrowserJSON(List<JSONObject> seleniumCapabilityRequests) {
			String config = Constant.NOT_AVAILABLE;
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				config = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(seleniumCapabilityRequests);
			} catch (JsonProcessingException e) {
				logger.warn(e.getMessage());
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
				int x=1;
				while(x!=-1) {
					x=stopTunnel();
				}
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}
			return super.tearDown(build, listener);
		}
		
		private int stopTunnel() throws IOException, InterruptedException {
			if (tunnelProcess != null && tunnelProcess.isAlive()) {
				logger.info("tunnel is active, going to stop tunnel binary");
				long tunnelProcessId=getPidOfProcess(tunnelProcess);
				stopTunnelProcessUsingPID(tunnelProcessId);
				Thread.sleep(2000);
				return 10;
			}else {
				logger.info("Tunnel Stopped");
				return -1;
			}
		}
		
		private long getPidOfProcess(Process p) {
		    long pid = -1;
		    try {
		      if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
		        Field f = p.getClass().getDeclaredField("pid");
		        f.setAccessible(true);
		        pid = f.getLong(p);
		        f.setAccessible(false);
		      }
		    } catch (Exception e) {
		      pid = -1;
		    }
		    return pid;
		  }
		
		private void stopTunnelProcessUsingPID(long tunnelProcessId) throws IOException {
			Runtime.getRuntime().exec("kill -SIGINT "+ tunnelProcessId);
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
