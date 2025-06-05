package com.sunseed.simtool.model;

import com.sunseed.simtool.constant.ServerType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseServer {

	private String serverName;
	private String type;
	private String username;
	private String password;
	private String host;
	private String identityFile;
	private ServerType serverType;

	protected BaseServer(ServerType serverType, String serverName, String type, String username, String password,
			String host, String identityFile) {
		this.serverType = serverType;
		this.serverName = serverName;
		this.type = type;
		this.username = username;
		this.password = password;
		this.host = host;
		this.identityFile = identityFile;
	}

	// If needed for servers without identityFile
	protected BaseServer(ServerType serverType, String serverName, String type, String username, String password,
			String host) {
		this(serverType, serverName, type, username, password, host, null);
	}
}
