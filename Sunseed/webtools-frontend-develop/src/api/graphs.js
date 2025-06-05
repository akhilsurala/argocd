import { BASE_URL } from "./config";
import axios from "./interceptor";

export function getDeltaGraphData(projectId, payload) {
  return axios.put(
    `${BASE_URL}/project/${projectId}/acrossRuns/keyDeltaGraph`,
    payload,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    }
  );
}

export function getCrops() {
  return axios.get(`${BASE_URL}/crops`, {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });
}

export function get2DGraphData(projectId, runId) {
  return axios.get(
    `${BASE_URL}/project/${projectId}/withinRuns/2dGraph?runId=${runId}`,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    }
  );
}

export function getGraphFilters(projectId, payload) {
  // console.log("API: ", "getGraphFilters");
  // console.log("Project ID: ", projectId);
  // console.log("Payload: ", payload);

  return axios.put(
    `${BASE_URL}/project/${projectId}/postprocessing-details?dataType=${payload.dataType}&frequency=${payload.frequency}`,
    { runIds: payload.runIds },
    // { runIds: [122] },
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    }
  );
}

// TODO: Dummy url, to be changed.
export function getGraphData(projectId, runId, payload) {
  // console.log("API: ", "getGraphData");
  // console.log("Project ID: ", projectId);
  // console.log("Run ID: ", runId);
  // console.log("Payload: ", payload);

  return axios.put(
    `${BASE_URL}/project/${projectId}/hourly-details?quantity=${payload.quantityAvailable}&dataType=${payload.dataType}&frequency=${payload.frequency}`,
    payload,
    {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      params: { runId }, // Optional: include runId as a query parameter if needed
    }
  );
}
