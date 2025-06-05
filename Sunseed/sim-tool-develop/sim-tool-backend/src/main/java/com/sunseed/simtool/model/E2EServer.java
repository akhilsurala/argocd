package com.sunseed.simtool.model;

import java.math.BigDecimal;

import com.sunseed.simtool.constant.ServerType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class E2EServer extends BaseServer{

	private int cpuCount;
	private BigDecimal load;
	private Long nodeId;
	
	public E2EServer(String serverName, String type, Integer cpuCount,BigDecimal load, String username,
			String password, String host, String identityFile,Long nodeId) {
		super(ServerType.E2E, serverName, type, username, password, host, identityFile);
		this.cpuCount = cpuCount.intValue();
		this.load = load;
		this.nodeId = nodeId;
	}

	public E2EServer(String serverName, String type, Integer cpuCount,BigDecimal load,String username,
			String password, String host,Long nodeId) {
		super(ServerType.E2E, serverName, type, username, password, host);
		this.cpuCount = cpuCount.intValue();
		this.load = load;
		this.nodeId = nodeId;
	}
}
