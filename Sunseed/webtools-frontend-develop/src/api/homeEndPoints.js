import { BASE_URL } from './config.js';
import axios from './interceptor';

export function getHomeDashboard(data, cancelToken) {
  return axios.put(`${BASE_URL}/home`, data, cancelToken, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}