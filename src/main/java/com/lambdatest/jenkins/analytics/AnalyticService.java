package com.lambdatest.jenkins.analytics;


import com.lambdatest.jenkins.analytics.data.AnalyticRequest;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.service.CapabilityService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnalyticService implements Runnable {

	//private final static Logger logger = Logger.getLogger(CapabilityService.class.getName());
	private static final Logger logger = LogManager.getLogger(AnalyticService.class);

	private AnalyticRequest analyticRequest;

	@Override
	public void run() {
		try {
			CapabilityService.sendPostRequest(Constant.ANALYTICS_URL, analyticRequest);
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
	}

	public AnalyticRequest getAnalyticRequest() {
		return analyticRequest;
	}

	public void setAnalyticRequest(AnalyticRequest analyticRequest) {
		this.analyticRequest = analyticRequest;
	}

}
