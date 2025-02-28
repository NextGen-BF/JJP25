import axios from "axios";
import { createAsyncThunk } from "@reduxjs/toolkit";

type VerifyPayload = {
  email: string;
  verificationCode: string;
};

export const registerUser = createAsyncThunk(
  "verify/VerifyUser",
  async (verifyData: VerifyPayload, { rejectWithValue }) => {
    try {
      const url = import.meta.env.VITE_VERIFY_URL;
      const response = await axios.post(url, verifyData);
      return response.data;
    } catch (error: any) {
      if (axios.isAxiosError(error) && error.response) {
        return rejectWithValue(error.response.data);
      }
      return rejectWithValue("An unexpected error occurred.");
    }
  }
);
