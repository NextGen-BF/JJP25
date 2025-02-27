import axiosInstance from "../../api/axiosInstance";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { RootState } from "../store";

export const submitEventPayload = createAsyncThunk(
  "events/submitEvent",
  async (_, { getState, rejectWithValue }) => {
    const state = getState() as RootState;

    const eventData = state.event.event;
    const venueData = state.venue.venue;
    const ticketData = state.ticket.tickets;

    const formattedEventData = {
      ...eventData,
      dates: eventData.dates.map((date) => date.format("HH:mm DD/MM/YYYY")),
    };

    const eventPayload = {
      event: formattedEventData,
      venue: venueData,
      tickets: ticketData,
    };

    try {
      const response = await axiosInstance.post("/events", eventPayload, {});

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
