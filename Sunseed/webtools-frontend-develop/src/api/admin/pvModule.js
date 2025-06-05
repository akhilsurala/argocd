import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminPvModules(searchText) {
  return axios.get(`${BASE_URL}/admin/pvModules?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminPvModule(payload) {
  return axios.post(`${BASE_URL}/admin/pvModule`, payload, {
    headers: {
      'Content-Type': "multipart/form-data",
    },
  });
}

export function updateAdminPvModule(id, payload) {
  return axios.put(`${BASE_URL}/admin/pvModule/${id}`, payload, {
    headers: {
      'Content-Type': "multipart/form-data",
    },
  });
}

export function getAdminPvModule(id) {
  return axios.get(`${BASE_URL}/admin/pvModule/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function hideAdminPvModule(id) {
  return axios.delete(`${BASE_URL}/admin/pvModule/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}