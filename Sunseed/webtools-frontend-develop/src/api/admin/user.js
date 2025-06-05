import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminUsers(searchText) {
  return axios.get(`${BASE_URL}/admin/users?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminUser(payload) {
  return axios.post(`${BASE_URL}/admin/adminSignup`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function updateAdminUser(id, payload) {
  return axios.put(`${BASE_URL}/admin/users/${id}`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function getAdminUser(id) {
  return axios.get(`${BASE_URL}/admin/users/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function blockAdminUser(id, paylaod) {
  return axios.put(`${BASE_URL}/admin/users/block/${id}`, paylaod, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}