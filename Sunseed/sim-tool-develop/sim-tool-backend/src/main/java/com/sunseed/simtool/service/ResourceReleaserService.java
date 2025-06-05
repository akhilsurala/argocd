package com.sunseed.simtool.service;

import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.model.BaseServer;

public interface ResourceReleaserService {

	public void finallyReleasingServerResources(BaseServer baseServer,SimulationTask simulationTask);
}
