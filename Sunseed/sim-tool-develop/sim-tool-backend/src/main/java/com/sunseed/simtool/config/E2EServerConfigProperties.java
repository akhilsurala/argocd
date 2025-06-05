package com.sunseed.simtool.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.sunseed.simtool.model.E2EServerConfig;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@ConfigurationProperties(prefix = "e2e.server")
@Getter
@Slf4j
public class E2EServerConfigProperties {

	private final List<String> gpuType = new ArrayList<>();
	private final List<String> osversion = new ArrayList<>();
	private final List<String> os = new ArrayList<>();
	private final List<String> category = new ArrayList<>();
	private final List<String> apiKey = new ArrayList<>();
	private final List<String> authToken = new ArrayList<>();
	private final List<String> username = new ArrayList<>();
	private final List<String> password = new ArrayList<>();
	private final List<String> location = new ArrayList<>();
	private final List<Integer> projectId = new ArrayList<>();
	private final List<String> savedImageTemplateId = new ArrayList<>();

	@PostConstruct
	public void validate() {
		int size = gpuType.size();

		if (!(osversion.size() == size && os.size() == size && category.size() == size && apiKey.size() == size
				&& authToken.size() == size && username.size() == size && password.size() == size
				&& location.size() == size && projectId.size() == size && savedImageTemplateId.size() == size)) {
			// Log all the values and sizes in a single log statement
			log.debug(
					"Mismatch found! List sizes: gpuType={}, osversion={}, os={}, category={}, apiKey={}, authToken={}, username={}, password={}, location={}, projectId={}, savedImageTemplateId = {}",
					gpuType.size(), osversion.size(), os.size(), category.size(), apiKey.size(), authToken.size(),
					username.size(), password.size(), location.size(), projectId.size(), savedImageTemplateId.size());
			throw new IllegalStateException("All config lists must be of the same length.");
		}
	}

	public List<E2EServerConfig> getServerConfigList() {
		List<E2EServerConfig> list = new ArrayList<>();
		for (int i = 0; i < gpuType.size(); i++) {
			// Make ServerConfig immutable with constructor
			list.add(new E2EServerConfig(gpuType.get(i), osversion.get(i), os.get(i), category.get(i), apiKey.get(i),
					authToken.get(i), username.get(i), password.get(i), location.get(i), projectId.get(i),
					savedImageTemplateId.get(i)));
		}
		return list;
	}
}