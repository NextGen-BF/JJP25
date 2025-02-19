import axiosInstance from "../../api/axiosInstance";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { Event } from "../slices/eventSlice";

// TODO:
export const submitEvent = createAsyncThunk(
  "event/submitEvent",
  async (event: Event, { rejectWithValue }) => {
    const formattedEvent = {
      ...event,
      dates: event.dates.map((date) => date.toISOString()),
    };

    try {
      const response = await axiosInstance.post("/events", formattedEvent, {
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
