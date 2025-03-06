import axios from "axios";
import { auth } from "../firebase/firebase";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// // If we want to add auth tokens and some optimizations
// axiosInstance.interceptors.request.use();
// axiosInstance.interceptors.response.use();

axios.interceptors.request.use(
  async (config) => {
    const user = auth.currentUser;

    if (user) {
      const token = await user.getIdToken();
      config.headers.Authorization = `Bearer ${token}`;
    } else {
      //here should be added the system authorization token
    }

    return config;
  },
  (error) => Promise.reject(error)
);

export default axiosInstance;
