import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Dayjs } from "dayjs";
import { submitEventPayload } from "../services/eventPayloadService";

export enum EventCategory {
  CONCERT = "Concert",
  SPORT = "Sport",
  SEMINAR = "Seminar",
  THEATER = "Theater",
  COMEDY_SHOW = "Comedy Show",
  FILM_SCREENING = "Film Screening",
  WORKSHOP = "Workshop",
}

export enum AgeRestriction {
  ALL_AGES = "All Ages",
  EIGHTEEN_PLUS = "18+",
  TWENTY_ONE_PLUS = "21+",
}

export interface Event {
  title: string;
  description: string;
  dates: Dayjs[];
  venueTitle?: string;
  category?: EventCategory;
  ageRestriction?: AgeRestriction;
}

export interface EventState {
  event: Event;
  isNewEvent: boolean;
  status: "idle" | "loading" | "succeeded" | "failed";
  error: string | null;
}

export const initialState: EventState = {
  event: {
    title: "",
    description: "",
    dates: [],
    venueTitle: "",
    category: undefined,
    ageRestriction: AgeRestriction.ALL_AGES,
  },
  isNewEvent: true,
  status: "idle",
  error: null,
};

const eventSlice = createSlice({
  name: "event",
  initialState,
  reducers: {
    updateEvent: (state, action: PayloadAction<Partial<Event>>) => {
      state.event = Object.assign({}, state.event, action.payload);
    },

    createEvent: (state, action: PayloadAction<Event>) => {
      state.event = action.payload;
      state.isNewEvent = false;
    },

    resetEvent: (state) => {
      state.event = initialState.event;
      state.isNewEvent = true;
      state.status = "idle";
      state.error = null;
    },
  },

  // TODO:
  extraReducers: (builder) => {
    builder
      .addCase(submitEventPayload.pending, (state) => {
        state.status = "loading";
      })
      .addCase(submitEventPayload.fulfilled, (state) => {
        state.status = "succeeded";
      })
      .addCase(submitEventPayload.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.payload as string;
      });
  },
});

export const { updateEvent, createEvent, resetEvent } = eventSlice.actions;
export default eventSlice.reducer;
