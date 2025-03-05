import { createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";

export const resendCode= createAsyncThunk(
  "user/resendCode",
  async (email: string, { rejectWithValue }) => {
    try {

      const response = await axios.post(
        import.meta.env.VITE_API_URL + import.meta.env.VITE_RESEND_URL + email
      );
      return response.data;
    } catch (error: any) {
      if (axios.isAxiosError(error) && error.response) {
        return rejectWithValue(error.response.data);
      }
      return rejectWithValue("An unexpected error occurred.");
    }
  }
);
