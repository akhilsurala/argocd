import { BASE_URL } from './config';
import axios from './interceptor';

export function getEconominParameters(projectId, runId) {
    return axios.get(`${BASE_URL}/project/${projectId}/economicParameters?runId=${runId || ""}`, {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });
  };
export function addEconominParameters(payload, projectId, runId) {
    return axios.post(`${BASE_URL}/project/${projectId}/economicParameters?runId=${runId || ""}`, payload,{
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });
  };
export function updateEconominParameters(payload, projectId, economicParameterId, runId) {
    return axios.put(`${BASE_URL}/project/${projectId}/economicParameters/${economicParameterId}?runId=${runId || ""}`, payload,{
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
    });
  };