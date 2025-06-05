import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminModeOfPvOperations(searchText) {
  return axios.get(`${BASE_URL}/admin/modeOfPvOperations?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminModeOfPvOperation(payload) {
  return axios.post(`${BASE_URL}/admin/modeOfPvOperation`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function updateAdminModeOfPvOperation(id, payload) {
  return axios.put(`${BASE_URL}/admin/modeOfPvOperation/${id}`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function getAdminModeOfPvOperation(id) {
  return axios.get(`${BASE_URL}/admin/modeOfPvOperation/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function hideAdminModeOfPvOperation(id) {
  return axios.delete(`${BASE_URL}/admin/modeOfPvOperation/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}