import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminDashboardData() {
    return axios.get(`${BASE_URL}/admin/pvModules`, {
      headers: {
          'Content-Type': 'application/json',
      },
    });
}

export function getAdminDashboardCount() {
    return axios.get(`${BASE_URL}/admin/home`, {
      headers: {
          'Content-Type': 'application/json',
      },
    });
}

export function addToRunningState(projectId,runId) {
    return axios.put(`${BASE_URL}/project/${projectId}/runs/${runId}/simulate`, {
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }