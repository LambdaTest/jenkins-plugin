package com.lambdatest.jenkins.freestyle.api.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserAuthResponse {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("username")
	private String username;
	@JsonProperty("email")
	private String email;
	@JsonProperty("status")
	private String status;
	@JsonProperty("organization_role")
	private String organizationRole;
	@JsonProperty("type")
	private String type;
	@JsonProperty("message")
	private String message;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrganizationRole() {
		return organizationRole;
	}

	public void setOrganizationRole(String organizationRole) {
		this.organizationRole = organizationRole;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
