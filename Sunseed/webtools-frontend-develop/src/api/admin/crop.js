import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminCrops(searchText) {
  return axios.get(`${BASE_URL}/admin/crops?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminCrop(payload) {
  return axios.post(`${BASE_URL}/admin/crop`, payload, {
    headers: {
      'Content-Type': "multipart/form-data",
    },
  });
}

export function updateAdminCrop(id, payload) {
  return axios.put(`${BASE_URL}/admin/crop/${id}`, payload, {
    headers: {
      'Content-Type': "multipart/form-data",
    },
  });
}

export function getAdminCrop(id) {
  return axios.get(`${BASE_URL}/admin/crop/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function hideAdminCrop(id) {
  return axios.delete(`${BASE_URL}/admin/crop/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}