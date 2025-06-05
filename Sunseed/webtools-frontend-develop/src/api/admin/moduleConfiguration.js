import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminModuleConfigurations(searchText) {
  return axios.get(`${BASE_URL}/admin/pvModuleConfigurations?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminModuleConfiguration(payload) {
  return axios.post(`${BASE_URL}/admin/pvModuleConfiguration`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function updateAdminModuleConfiguration(id, payload) {
  return axios.put(`${BASE_URL}/admin/pvModuleConfiguration/${id}`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function getAdminModuleConfiguration(id) {
  return axios.get(`${BASE_URL}/admin/pvModuleConfiguration/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function hideAdminModuleConfiguration(id) {
  return axios.delete(`${BASE_URL}/admin/pvModuleConfiguration/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}