import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Dayjs } from "dayjs";
import { submitEvent } from "../services/eventService";

export enum VenueType {
  STADIUM = "Stadium",
  CONFERENCE_CENTRE = "Conference Centre",
  CINEMA_HALL = "Cinema Hall",
  THEATER = "Theater",
  RESTAURANT = "Restaurant",
  CLUB = "Club",
  HOTEL = "Hotel",
  OUTDOOR = "Outdoor",
}

export interface Venue {
  name: string;
  type?: VenueType;
  country: string;
  city: string;
  address: string;
  phone: string;
  website: string;
}

export interface VenueState {
  venue: Venue;
  isNewVenue: boolean;
  status: "idle" | "loading" | "succeeded" | "failed";
  error: string | null;
}

const initialState: VenueState = {
  venue: {
    name: "",
    type: undefined,
    country: "",
    city: "",
    address: "",
    phone: "",
    website: "",
  },
  isNewVenue: true,
  status: "idle",
  error: null,
};

const venueSlice = createSlice({
  name: "venue",
  initialState,
  reducers: {
    updateVenue: (state, action: PayloadAction<Partial<Venue>>) => {
      state.venue = Object.assign({}, state.venue, action.payload);
    },

    createVenue: (state, action: PayloadAction<Venue>) => {
      state.venue = action.payload;
      state.isNewVenue = false;
    },

    resetVenue: (state) => {
      state.venue = initialState.venue;
      state.isNewVenue = true;
      state.status = "idle";
      state.error = null;
    },
  },

  extraReducers: (builder) => {
    builder
      .addCase(submitEvent.pending, (state) => {
        state.status = "loading";
      })
      .addCase(submitEvent.fulfilled, (state) => {
        state.status = "succeeded";
      })
      .addCase(submitEvent.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.payload as string;
      });
  },
});

export const { updateVenue, createVenue, resetVenue } = venueSlice.actions;
export default venueSlice.reducer;
