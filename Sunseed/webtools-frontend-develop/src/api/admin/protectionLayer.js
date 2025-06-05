import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminProtectionLayers(searchText) {
  return axios.get(`${BASE_URL}/admin/protectionLayers?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminProtectionLayer(payload) {
  return axios.post(`${BASE_URL}/admin/protectionLayer`, payload, {
    headers: {
      'Content-Type': "multipart/form-data",
    },
  });
}

export function updateAdminProtectionLayer(id, payload) {
  return axios.put(`${BASE_URL}/admin/protectionLayer/${id}`, payload, {
    headers: {
      'Content-Type': "multipart/form-data",
    },
  });
}

export function getAdminProtectionLayer(id) {
  return axios.get(`${BASE_URL}/admin/protectionLayer/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function hideAdminProtectionLayer(id) {
  return axios.delete(`${BASE_URL}/admin/protectionLayer/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}