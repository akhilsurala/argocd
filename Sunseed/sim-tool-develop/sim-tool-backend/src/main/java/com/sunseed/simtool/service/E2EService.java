package com.sunseed.simtool.service;

import com.sunseed.simtool.constant.SceneType;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.model.BaseServer;

public interface E2EService {
	
	public BaseServer getRunnableServer(SimulationTask simulationTask, SceneType sceneType);
}
