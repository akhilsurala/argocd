import { BASE_URL } from "./config";
import axios from "./interceptor";

export function getAllRunsData(projectId, bayStatus,searchText='') {
  return axios.get(`${BASE_URL}/project/${projectId}/runs?bay=${bayStatus}&searchText=${encodeURIComponent(searchText)}`, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}

// Need to be modified once API becomes available.
export function getChildRunsData(projectId, bayStatus, runId) {
  return axios.get(`${BASE_URL}/project/${projectId}/runs/${runId}/variants`, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}

export function addToRunningState(projectId, runId) {
  return axios.put(`${BASE_URL}/project/${projectId}/runs/simulate`, runId, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}

export function runStatusApi(runId, payload) {
  return axios.put(`${BASE_URL}/run/${runId}/status`, payload, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}

export function downloadSimulationReport(simulationId) {
  return axios.get(`${BASE_URL}/simulation?id=${simulationId}`, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}

export function deleteRun(projectId, runId) {
  return axios.delete(`${BASE_URL}/project/${projectId}/run/${runId}`, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}

export function setControlFlag(projectId, runId, agriControl, pvControl) {
  return axios.put(
    `${BASE_URL}/projects/${projectId}/runs/${runId}/agri-pv-control?agriControl=${agriControl}&pvControl=${pvControl}`,
    {},
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
}

export function getRunNames(projectId, payload) {
  return axios.put(`${BASE_URL}/projects/${projectId}/runs/get-run-names`, payload, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}

export function getDesignExplorerData(projectId, payload) {
  return axios.put(`${BASE_URL}/projects/${projectId}/runs/design-explorer`, payload, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}


export function getExportPdf(projectId,runIds, payload) {
  return axios.get(`${BASE_URL}/project/${projectId}/runs/report?runIds=${runIds}`, payload, {
    headers: {
      "Content-Type": "application/json",
    },
  });
}

