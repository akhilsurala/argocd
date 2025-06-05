import { getApvToggle } from '../utils/constant';
import { BASE_URL } from './config';
import axios from './interceptor';

export function addPvParameters(data,projectId,apvToggle, runId, isCloned, isMaster) {
  return axios.post(`${BASE_URL}/project/${projectId}/pvParameters?toggle=${getApvToggle(apvToggle)}&runId=${runId || ""}&isCloned=${isCloned || ""}&isMaster=${isMaster || ""}`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function UpdatePvParameters(data,projectId,pvParametersId,apvToggle, runId) {
  return axios.put(`${BASE_URL}/project/${projectId}/pvParameters/${pvParametersId}?toggle=${getApvToggle(apvToggle)}&runId=${runId || ""}`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}


export function getPvParameters(projectId, runId, isCloned) {
    return axios.get(`${BASE_URL}/project/${projectId}/pvParameters?runId=${runId || ""}&isCloned=${isCloned || ""}`,{
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

export function getPvParametersDropDownData (mode) {
  return axios.get(`${BASE_URL}/project/pvParameters/master`,{
    params: {
      mode: mode
  },
    headers: {
      'Content-Type': 'application/json',
    },
  });
}
