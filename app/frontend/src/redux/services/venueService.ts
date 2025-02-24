import axiosInstance from "../../api/axiosInstance";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { Venue } from "../slices/venueSlice";

// TODO:
export const submitVenue = createAsyncThunk(
  "event/submitVenue",
  async (venue: Venue, { rejectWithValue }) => {
    try {
      const response = await axiosInstance.post("/venues", venue, {
        headers: {
          "Content-Type": "application/json",
        },
      });

      return response.data;
    } catch (error: any) {
      const errorMessage =
        error.response?.data?.message ||
        error.message ||
        "Something went wrong";
      return rejectWithValue(errorMessage);
    }
  }
);
