package com.sunseed.enums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RunBayStatus {

	RUNNING("running"), HOLDING("holding");

	private String value;
	private static Map<RunBayStatus, List<RunStatus>> runMap = fillRunMap();

	private RunBayStatus(String value) {
		this.value = value;
	}
	
	@JsonValue
    public String getValue() {
        return value;
    }

	public static Map<RunBayStatus, List<RunStatus>> fillRunMap() {

		// add next key-value pair according to new key decided by frontEnd
		Map<RunBayStatus, List<RunStatus>> runMap = new HashMap<>();
		runMap.put(RunBayStatus.RUNNING, List.of(RunStatus.RUNNING, RunStatus.PAUSED,
				RunStatus.COMPLETED, RunStatus.FAILED,RunStatus.QUEUED));
		runMap.put(RunBayStatus.HOLDING, List.of(RunStatus.HOLDING, RunStatus.RUNNING, RunStatus.PAUSED,
				RunStatus.COMPLETED, RunStatus.FAILED,RunStatus.QUEUED));
		return runMap;
	}
	
	public static List<RunStatus> getListOfRunStatus(String value){
		
		for (RunBayStatus runBayStatus : RunBayStatus.values()) {
			if (runBayStatus.getValue().equalsIgnoreCase(value)) {
				return runMap.get(runBayStatus);
			}
		}
		return null;
	}
}
