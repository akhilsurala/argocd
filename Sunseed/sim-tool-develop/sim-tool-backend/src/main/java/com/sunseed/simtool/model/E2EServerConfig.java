package com.sunseed.simtool.model;

import lombok.Getter;

@Getter
public class E2EServerConfig {

	private final String gpuType;
	private final String osVersion;
	private final String os;
	private final String category;
	private final String apiKey;
	private final String authToken;
	private final String username;
	private final String password;
	private final String location;
	private final Integer projectId;
	private final String savedImageTemplateId;

	// Constructor to set all final fields
	public E2EServerConfig(String gpuType, String osVersion, String os, String category, String apiKey,
			String authToken, String username, String password, String location, Integer projectId,
			String savedImageTemplateId) {
		this.gpuType = gpuType;
		this.osVersion = osVersion;
		this.os = os;
		this.category = category;
		this.apiKey = apiKey;
		this.authToken = authToken;
		this.username = username;
		this.password = password;
		this.location = location;
		this.projectId = projectId;
		this.savedImageTemplateId = savedImageTemplateId;
	}
}
