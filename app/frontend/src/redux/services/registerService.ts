import { createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";

type RegisterPayload = {
  email: string;
  password: string;
  confirmPassword: string;
  role: string;
  username: string;
  firstName: string;
  lastName: string;
  birthDate: string;
};

export const registerUser = createAsyncThunk(
  "registerData/register",
  async (userData: RegisterPayload, { rejectWithValue }) => {
    try {
      const url = import.meta.env.VITE_API_URL + import.meta.env.VITE_REGISTER_URL;
      const response = await axios.post(url, userData);
      return response.data;
    } catch (error: any) {
      if (axios.isAxiosError(error) && error.response) {
        return rejectWithValue(error.response.data);
      }
      return rejectWithValue("An unexpected error occurred.");
    }
  }
);
