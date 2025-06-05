import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminSoils(searchText) {
  return axios.get(`${BASE_URL}/admin/soils?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminSoil(payload) {
  return axios.post(`${BASE_URL}/admin/soil?name=${payload.name}&hide=true`, payload, {
    headers: {
      'Content-Type': "multipart/form-data",
    },
  });
}

export function updateAdminSoil(id, payload) {
  const data = JSON.parse(Object.fromEntries(payload).requestDto);
  console.log(data);
  const name = data?.name;
  const hide = data?.hide;
  return axios.put(`${BASE_URL}/admin/soil/${id}?name=${name}&hide=${hide}`, payload, {
    headers: {
      'Content-Type': "multipart/form-data",
    },
  });
}

export function getAdminSoil(id) {
  return axios.get(`${BASE_URL}/admin/soil/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function hideAdminSoil(id) {
  return axios.delete(`${BASE_URL}/admin/soil/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}