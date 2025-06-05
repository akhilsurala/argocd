import { BASE_URL } from './config';
import axios from './interceptor';

export function callSignUp(data, cancelToken) {
  return axios.post(`${BASE_URL}/signup`, data, cancelToken, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}
export function sendVerifyEmail(data) {
  return axios.post(`${BASE_URL}/otp/send`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function checkVerifyEmail(data) {
  return axios.post(`${BASE_URL}/otp/verify`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function signInApi(data) {
  return axios.post(`${BASE_URL}/login`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

export function forgotPasswordApi(data) {
  return axios.put(`${BASE_URL}/forgot-password`, data, {
    headers: {
      'Content-Type': 'application/json',
    },
  });
}

