import axios from "axios";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// // If we want to add auth tokens and some optimizations
// axiosInstance.interceptors.request.use();
// axiosInstance.interceptors.response.use();

export default axiosInstance;
