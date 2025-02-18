import { createAsyncThunk } from "@reduxjs/toolkit"
import dayjs, { Dayjs } from "dayjs";

export enum EventCategory {
    NOT_SELECTED = "Not Selected",
    CONCERT = "Concert",
    SPORT = "Sport",
    SEMINAR = "Seminar",
    THEATER = "Theater",
    COMEDY_SHOW = "Comedy Show",
    FILM_SCREENING = "Film Screening",
    WORKSHOP = "Workshop",
}

export interface Event {
    title: string;
    description: string;
    dates: Dayjs[];
    venue?: string;
    category: EventCategory;
    ageRestriction?: number | null;
}

export interface EventState {
    event: Event;
    status: "idle" | "loading" | "succeeded" | "failed";
    error: string | null;
}

export const initialState: EventState = {
    event: {
        title: "",
        description: "",
        dates: [],
        venue: "",
        category: EventCategory.NOT_SELECTED,
        ageRestriction: null,
      },
      status: "idle",
      error: null,
}

// TODO:
export const fetchVenues = createAsyncThunk("event/fetchVenues", async () => {
    const response = await fetch("");

    if(!response.ok) {

    }
});

// TODO:
export const submitEvent = createAsyncThunk("event/submitEvent", async (event: Event) => {
    const formattedEvent = {
        ...event,
        dates: event.dates.map(date => date.toISOString()), // Convert Dayjs to string
    };

    try {
        const response = await fetch("");
    } catch (error: unknown) {

    }
}
)