package com.lambdatest.jenkins.analytics;

import java.util.logging.Logger;

import com.lambdatest.jenkins.analytics.data.AnalyticRequest;
import com.lambdatest.jenkins.freestyle.api.Constant;
import com.lambdatest.jenkins.freestyle.api.service.CapabilityService;

public class AnalyticService implements Runnable {

	private final static Logger logger = Logger.getLogger(CapabilityService.class.getName());

	private AnalyticRequest analyticRequest;

	@Override
	public void run() {
		try {
			CapabilityService.sendPostRequest(Constant.ANALYTICS_URL, analyticRequest);
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
	}

	public AnalyticRequest getAnalyticRequest() {
		return analyticRequest;
	}

	public void setAnalyticRequest(AnalyticRequest analyticRequest) {
		this.analyticRequest = analyticRequest;
	}

}
