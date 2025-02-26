import axiosInstance from "../../api/axiosInstance";
import { createAsyncThunk } from "@reduxjs/toolkit";
import { Event } from "../slices/eventSlice";
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
      dates: eventData.dates.map((date) => date.toISOString()),
    };

    const eventPayload = {
      event: formattedEventData,
      venue: venueData,
      tickets: ticketData,
    };

    try {
      const response = await axiosInstance.post("/events", eventPayload, {
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
