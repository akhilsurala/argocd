package com.sunseed.simtool.service;

import java.util.List;
import java.util.Optional;

import com.sunseed.simtool.entity.E2EMachineNode;
import com.sunseed.simtool.model.E2EServerConfig;

public interface MachineProvisionService {

	public Optional<E2EMachineNode> provisionMachine(List<E2EServerConfig> properties, int requiredGPUCount);
}
