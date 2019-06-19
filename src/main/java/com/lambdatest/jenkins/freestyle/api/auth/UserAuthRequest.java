package com.lambdatest.jenkins.freestyle.api.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAuthRequest {

	@JsonProperty("username")
	private String username;
	@JsonProperty("token")
	private String token;
	
	public UserAuthRequest(String username, String token) {
		super();
		this.username = username;
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
