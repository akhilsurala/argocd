import { BASE_URL } from './config';
import axios from './interceptor';

export function updateUserProfile(data) {
  return axios.put(`${BASE_URL}/profile`, data, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function getUserProfileDetails() {
  return axios.get(`${BASE_URL}/profile`, {
    headers: {
      "Content-Type": 'application/json',
    },
  });
}

export function updatePassword(data) {
  return axios.put(`${BASE_URL}/change-password`, data, {
    headers: {
      "Content-Type": 'application/json',
    },
  });
}

export function getNotificationList(userProfileId) {
  return axios.get(`${BASE_URL}/notification/${userProfileId}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function markAllNotifications(data) {
  return axios.post(`${BASE_URL}/notification/status`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}


export function addProject(data,cancelToken) {
  return axios.post(`${BASE_URL}/project`, data,cancelToken, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}export function updateAgriGeneralParameter(data,cancelToken,projectId,id, runId) {
  return axios.put(`${BASE_URL}/project/${projectId}/agriGeneralParameters/${id}?runId=${runId || ""}`, data,cancelToken, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function addAgriGeneralParameter(data,cancelToken,projectId,runId) {
  return axios.post(`${BASE_URL}/project/${projectId}/agriGeneralParameters?runId=${runId || ""}`, data,cancelToken, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function addCropParameter(data,cancelToken,projectId, runId) {
  return axios.post(`${BASE_URL}/project/${projectId}/cropParameters?runId=${runId || ""}`, data,cancelToken, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function updateCropParameter(data,cancelToken,projectId, cropParaId, runId) {
  return axios.put(`${BASE_URL}/project/${projectId}/cropParameters/${cropParaId}?runId=${runId || ""}`, data,cancelToken, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}
export function getCropParameter(projectId, runId) {
  return axios.get(`${BASE_URL}/project/${projectId}/cropParameters?runId=${runId || ""}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function getCropMasterDataParameter() {
  return axios.get(`${BASE_URL}/crops`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}


export function updateProject(data,id) {
  return axios.put(`${BASE_URL}/project/${id}`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}


export function getProjects(cancelToken,searchBoxValue='') {
  return axios.get(`${BASE_URL}/projects?searchText=${searchBoxValue}`,cancelToken, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
}

export function getProjectDetails(projectId) {
  return axios.get(`${BASE_URL}/projects/${projectId}`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
}

export function deleteProject(projectId,runId) {
  return axios.delete(`${BASE_URL}/project/${projectId}`, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
}

export function getProjectsOfAgriGeneralParameters(cancelToken,projectId, runId) {
  return axios.get(`${BASE_URL}/project/${projectId}/agriGeneralParameters?runId=${runId || ""}`,cancelToken, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
}export function getProjectsOfAgriGeneralParametersMasterData(cancelToken,projectId) {
  return axios.get(`${BASE_URL}/project/agriGeneralParameters/master`,cancelToken, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
}