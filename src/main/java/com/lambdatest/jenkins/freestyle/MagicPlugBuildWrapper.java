package com.lambdatest.jenkins.freestyle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.lambdatest.jenkins.credential.MagicPlugCredentials;
import com.lambdatest.jenkins.credential.MagicPlugCredentialsImpl;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.ItemGroup;
import hudson.tasks.BuildWrapper;
import net.sf.json.JSONObject;

@SuppressWarnings("serial")
public class MagicPlugBuildWrapper extends BuildWrapper implements Serializable {

	private List<JSONObject> seleniumCapabilityRequests = null;
	private String username;
	private String authKey;
	private String gridURL;

	@DataBoundConstructor
	public MagicPlugBuildWrapper(StaplerRequest req, List<JSONObject> seleniumCapabilityRequest, String credentialsId,
			ItemGroup context) {
		try {
			System.out.println("MagicPlugBuildWrapper");
			System.out.println(credentialsId);
			if (seleniumCapabilityRequest == null) { // prevent null pointer
				System.out.println("No Data");
				this.seleniumCapabilityRequests = new ArrayList<JSONObject>();
			} else {
				System.out.println("Some Data");
				System.out.println(seleniumCapabilityRequest);
				this.seleniumCapabilityRequests = seleniumCapabilityRequest;
				setCredentials(credentialsId, context);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void setCredentials(String credentialsId, ItemGroup context) {
		final MagicPlugCredentials magicPlugCredential = MagicPlugCredentialsImpl.getCredentials(credentialsId,
				context);
		if (magicPlugCredential != null) {
			this.username = magicPlugCredential.getUserName();
			this.authKey = magicPlugCredential.getAuthKey();
			System.out.println(username + ":" + authKey);
			// TODO Verify Auth Token Once again

		} else {
			System.out.println("credentials not found");
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
		for (JSONObject seleniumCapabilityRequest : seleniumCapabilityRequests) {
			makeSeleniumBuildActionFromJSONObject(build, buildname, buildnumber, seleniumCapabilityRequest);
		}
		System.out.println("Adding LT actions done");

		// Create Grid URL
		this.gridURL = createGridURL(this.username, this.authKey);
		System.out.println(this.gridURL);
		System.out.println("Environment setUp() -end");
		return new MagicPlugEnvironment(build);
	}

	private String createGridURL(String username, String authKey) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://").append(username).append(":").append(authKey).append("@dev-ml.lambdatest.com/wd/hub");
		return sb.toString();
	}

	public static void makeSeleniumBuildActionFromJSONObject(AbstractBuild build, String buildname, String buildnumber,
			JSONObject seleniumCapabilityRequest) {
		String operatingSystem = seleniumCapabilityRequest.getString("operatingSystem");
		String browser = seleniumCapabilityRequest.getString("browser");
		String resolution = seleniumCapabilityRequest.getString("resolution");

		LambdaFreeStyleBuildAction lfsBuildAction = new LambdaFreeStyleBuildAction("SeleniumTest", operatingSystem,
				browser, resolution);
		lfsBuildAction.setBuild(build);
		lfsBuildAction.setBuildName(buildname);
		lfsBuildAction.setBuildNumber(buildnumber);
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
			System.out.println("buildEnvVars");
			String buildname = build.getFullDisplayName().substring(0,
					build.getFullDisplayName().length() - (String.valueOf(build.getNumber()).length() + 1));
			String buildnumber = String.valueOf(build.getNumber());
			for (JSONObject seleniumCapabilityRequest : seleniumCapabilityRequests) {
				env.put("LT_OPERATING_SYSTEM", seleniumCapabilityRequest.getString("operatingSystem"));
				env.put("LT_BROWSER_NAME", seleniumCapabilityRequest.getString("browser"));
				env.put("LT_BROWSER_VERSION", "67.0");
				env.put("LT_RESOLUTION", seleniumCapabilityRequest.getString("resolution"));
			}
			env.put("LT_GRID_URL", gridURL);
			env.put("LT_BUILD_NAME", buildname);
			env.put("LT_BUILD_NUMBER", buildnumber);
			System.out.println(env);
			super.buildEnvVars(env);
		}

		@Override
		public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
			/*
			 * Runs after the build
			 */
			System.out.println("tearDown");
			return super.tearDown(build, listener);
		}

	}

}
