import axios from "axios";
import { BASE_URL } from "./config";

export function getRunsDetails(projectId,bayType) {
  return axios.get(`${BASE_URL}/project/${projectId}/runs`, {
    params: {
      bay: bayType
    },
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function addToRunningState(projectId, runId) {
  return axios.put(`${BASE_URL}/project/${projectId}/runs/${runId}/simulate`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function downloadSimulationReport(projectId,runId) {
  return axios.get(`${BASE_URL}/project/${projectId}/runs/${runId}/report`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function stopRun(runId, payload = {status: "Pause"}) {
  return axios.put(`${BASE_URL}/run/${runId}/status`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function reStartRun(runId, payload = {status: "Resume"}) {
  return axios.put(`${BASE_URL}/run/${runId}/status`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function cancelRun(runId, payload = {status: "Cancel"}) {
  return axios.put(`${BASE_URL}/run/${runId}/status`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}


export function getLinksForScenes(projectId,runId) {
  return axios.get(`${BASE_URL}/project/${projectId}/runs/${runId}/scenes`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}


