import axios from "axios";
import { clearLocalStore } from "../utils/localStorage";

// Request interceptor
axios.interceptors.request.use(
  (config) => {
    // Modify the request config or perform pre-processing
    // e.g., add headers, modify data, etc.
    config.headers.Authorization = localStorage.getItem("apiToken")
      ? `Bearer ${localStorage.getItem("apiToken")}`
      : null;
    return config;
  },
  (error) =>
    // Handle request error
    Promise.reject(error)
);

// Response interceptor
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.data?.message) {
      const variableName = "errorMessage";
      window[variableName] = error?.response?.data?.message;
      window['snackBargColor'] = "#C14040";
      console.error("error", error);
      const event = new CustomEvent("windowVariableAdded", {
        detail: { variableName },
      });
      window.dispatchEvent(event);
    }
    if (error?.response?.status === 401) {
      // Token expired or unauthorized, clear storage
      clearLocalStore();
    }
    return Promise.reject(error);
  }
);

export default axios;

