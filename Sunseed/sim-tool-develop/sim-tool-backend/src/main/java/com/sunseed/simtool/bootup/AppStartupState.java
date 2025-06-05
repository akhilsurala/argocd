package com.sunseed.simtool.bootup;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

@Component
public class AppStartupState {

	private final AtomicBoolean initialized = new AtomicBoolean(false);

	public boolean isInitialized() {
		return initialized.get();
	}

	public void markInitialized() {
		initialized.set(true);
	}

	public void awaitInitialization() {
		while (!initialized.get()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException("Interrupted during init wait");
			}
		}
	}
}