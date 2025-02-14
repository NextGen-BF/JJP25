import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { initialState, fetchVenues , submitEvent , Event } from "../services/eventService";

const eventSlice = createSlice({
    name: "event",
    initialState,
    reducers: {
        updateEvent: (state, action: PayloadAction<Partial<Event>>) => {
            state.event = { ...state.event, ...action.payload };
        },

        resetEvent: (state) => {
            state.event = initialState.event;
            state.status = "idle";
            state.error = null;
        }
    },

    // TODO:
    extraReducers: (builder) => {
        // Fetch venues logic
        builder
            .addCase(fetchVenues.pending, (state) => {
                state.status = "loading";
            });

        // Submit event logic
        builder
            .addCase(submitEvent.pending, (state) => {
                state.status = "loading";
            });
    },
})

export const { updateEvent, resetEvent } = eventSlice.actions;
export default eventSlice.reducer;