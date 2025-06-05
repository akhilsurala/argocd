import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminTypeOfIrrigations(searchText) {
  return axios.get(`${BASE_URL}/admin/typeOfIrrigations?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminTypeOfIrrigation(payload) {
  return axios.post(`${BASE_URL}/admin/typeOfIrrigation`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function updateAdminTypeOfIrrigation(id, payload) {
  return axios.put(`${BASE_URL}/admin/typeOfIrrigation/${id}`, payload, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function getAdminTypeOfIrrigation(id) {
  return axios.get(`${BASE_URL}/admin/typeOfIrrigation/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function hideAdminTypeOfIrrigation(id) {
  return axios.delete(`${BASE_URL}/admin/typeOfIrrigation/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}