import axios from "axios";
import { BASE_URL } from "../config";

export function getAdminStaticPages(searchText) {
  return axios.get(`${BASE_URL}/admin/staticPage?search=${encodeURIComponent(searchText)}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function getStaticPages() {
  return axios.get(`${BASE_URL}/staticPage`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function saveAdminStaticPage(payload) {
  return axios.post(`${BASE_URL}/admin/staticPage`, payload, {
    headers: {
      "Content-Type": 'application/json',
    },
  });
}

export function updateAdminStaticPage(id, payload) {
  return axios.put(`${BASE_URL}/admin/staticPage/${id}`, payload, {
    headers: {
      "Content-Type": 'application/json',
    },
  });
}

export function getAdminStaticPage(id) {
  return axios.get(`${BASE_URL}/admin/staticPage/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function hideAdminStaticPage(id) {
  return axios.delete(`${BASE_URL}/admin/staticPage/${id}`, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}