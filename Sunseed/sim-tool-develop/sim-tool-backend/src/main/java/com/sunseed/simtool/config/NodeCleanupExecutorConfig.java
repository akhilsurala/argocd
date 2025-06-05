package com.sunseed.simtool.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeCleanupExecutorConfig {

	@Bean(name = "nodeCleanupExecutor")
	public ExecutorService nodeCleanupExecutor() {
		return Executors.newSingleThreadExecutor(); // single-threaded cleanup
	}
}