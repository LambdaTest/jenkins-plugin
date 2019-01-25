package com.lambdatest.jenkins.freestyle.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.service.CapabilityService;
import com.lambdatest.jenkins.freestyle.exception.TunnelHashNotFoundException;

public class LambdaTunnelService {
	private final static Logger logger = Logger.getLogger(LambdaTunnelService.class.getName());

	protected static Process tunnelProcess;
	private static String tunnelFolderName = "/";

	public static Process setUp(String user, String key, String tunnelName) {
		if (OSValidator.isUnix()) {
			logger.info("Jenkins configured on Unix/Linux, getting latest hash");
			try {
				// Get Latest Hash
				String latestHash = getLatestHash(Constant.LINUX_HASH_URL);
				logger.info(latestHash);
				// Verify Latest binary version
				ClassLoader loader = LambdaTunnelService.class.getClassLoader();
				if (loader != null) {
					URL tunnelFolderPath = loader.getResource(tunnelFolderName);
					if (tunnelFolderPath != null) {
						String tunnelBinaryLocation = tunnelFolderPath.getPath() + latestHash + ".sh";
						logger.info("Tunnel Binary Location :" + tunnelBinaryLocation);
						File tunnelBinary = new File(tunnelBinaryLocation);
						if (tunnelBinary.exists()) {
							logger.info("Tunnel Binary already exists");
						} else {
							logger.info("Tunnel Binary not exists, downloading...");
							saveFileFromUrlWithJavaIO(tunnelBinaryLocation, Constant.LINUX_BINARY_URL);
							logger.info("Tunnel Binary downloaded from " + Constant.LINUX_BINARY_URL);
						}
						// Checking for the tunnel log file exists or not
						String tunnelLogPath = "/var/lib/jenkins/tunnel.log";
						logger.info("Tunnel Log Path:" + tunnelLogPath);
						File tunnelLogFile = new File(tunnelLogPath);
						if (tunnelLogFile.exists()) {
							logger.info("Tunnel log File already exists");
						} else {
							logger.info("Tunnel log File not exists");
							Runtime.getRuntime().exec("chmod 777 /var/lib/jenkins/");
							Runtime.getRuntime().exec("touch " + tunnelLogPath);
							logger.info("Tunnel log File created");
						}
						Runtime.getRuntime().exec("chmod 777 " + tunnelLogPath);
						return runCommandLine(tunnelBinaryLocation, tunnelLogPath, user, key,tunnelName);
					} else {
						logger.warning("tunnelFolderPath empty");
					}
				} else {
					logger.warning("loader empty");
				}
			} catch (Exception e) {
				logger.warning(e.getMessage());
			}
		} else if (OSValidator.isMac()) {
			logger.info("Jenkins configured on Mac, getting latest hash");
			try {
				// Get Latest Hash
				String latestHash = getLatestHash(Constant.MAC_HASH_URL);
				logger.info(latestHash);
				// Verify Latest binary version
				// Checking for the tunnel log file exists or not
				ClassLoader loader = LambdaTunnelService.class.getClassLoader();
				if (loader != null) {
					URL tunnelFolderPath = loader.getResource(tunnelFolderName);
					if (tunnelFolderPath != null) {
						String tunnelBinaryLocation = tunnelFolderPath.getPath() + latestHash + ".sh";
						logger.info("Tunnel Binary Location :" + tunnelBinaryLocation);
						File tunnelBinary = new File(tunnelBinaryLocation);
						if (tunnelBinary.exists()) {
							logger.info("Tunnel Binary already exists");
						} else {
							logger.info("Tunnel Binary not exists, downloading...");
							saveFileFromUrlWithJavaIO(tunnelBinaryLocation, Constant.MAC_BINARY_URL);
							logger.info("Tunnel Binary downloaded from " + Constant.MAC_BINARY_URL);
						}
						// Checking for the tunnel log file exists or not
						String tunnelLogPath = "tunnel.log";
						logger.info("Tunnel Log Path:" + tunnelLogPath);
						File tunnelLogFile = new File(tunnelLogPath);
						if (tunnelLogFile.exists()) {
							logger.info("Tunnel log File already exists");
						} else {
							logger.info("Tunnel log File not exists");
							Runtime.getRuntime().exec("touch " + tunnelLogPath);
							logger.info("Tunnel log File created");
						}
						Runtime.getRuntime().exec("chmod 777 " + tunnelLogPath);
						return runCommandLine(tunnelBinaryLocation, tunnelLogPath, user, key,tunnelName);
					} else {
						logger.warning("tunnelFolderPath empty");
					}
				} else {
					logger.warning("loader empty");
				}
			} catch (Exception e) {
				logger.warning(e.getMessage());
			}
		} else if (OSValidator.isWindows()) {
			logger.info("Jenkins configured on Windows");
		} else if (OSValidator.isSolaris()) {
			logger.info("Jenkins configured on Solaris");
		} else {
			logger.info("Tunnel Option Not Available for this configuration");
		}
		return null;
	}

	private static String getLatestHash(String url) throws TunnelHashNotFoundException {
		try {
			return CapabilityService.sendGetRequest(url);
		} catch (Exception e) {
			throw new TunnelHashNotFoundException(e.getMessage(), e);
		}
	}

	// Using Java IO
	public static void saveFileFromUrlWithJavaIO(String fileName, String fileUrl)
			throws MalformedURLException, IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			in = new BufferedInputStream(new URL(fileUrl).openStream());
			fout = new FileOutputStream(fileName);

			byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		} finally {
			if (in != null)
				in.close();
			if (fout != null)
				fout.close();
		}
	}

	public static Process runCommandLine(String filePath, String tunnelLogPath, String user, String key, String tunnelName)
			throws IOException {
		Runtime.getRuntime().exec("chmod 777 " + filePath);
		ProcessBuilder processBuilder = new ProcessBuilder(filePath, "-user", user, "-key", key, "-logFile",
				tunnelLogPath,"-tunnelName",tunnelName,"-v");
		Process tunnelProcess = processBuilder.start();
		Thread commandLineThread = new Thread(() -> {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(tunnelProcess.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					logger.info(line);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		commandLineThread.setDaemon(true);
		commandLineThread.start();
		logger.info("Tunnel Binary Executed");
		return tunnelProcess;

	}
}
