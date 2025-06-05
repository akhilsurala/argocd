package com.sunseed.simtool.config;

import java.util.concurrent.Semaphore;

import com.sunseed.simtool.constant.ServerType;
import com.sunseed.simtool.model.BaseServer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimulationServer extends BaseServer {

	private Semaphore cpu;

	public SimulationServer(String serverName, String type, Integer cpuTaskLimit, String username,
			String password, String host, String identityFile) {
		super(ServerType.Local, serverName, type, username, password, host, identityFile);
		this.cpu = new Semaphore(cpuTaskLimit);
	}

	public SimulationServer(String serverName, String type, Integer cpuTaskLimit, String username,
			String password, String host) {
		super(ServerType.Local, serverName, type, username, password, host);
		this.cpu = new Semaphore(cpuTaskLimit);
	}
}