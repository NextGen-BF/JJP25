import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export enum TicketType {
  GENERAL_ADMISSION = "General Admission",
  SEAT = "Seat",
  VIP = "VIP",
}

export interface Ticket {
  description: string;
  eventDate: string;
  ticketType: TicketType;
  price: number;
  totalQuantity: number;
}

interface TicketState {
  tickets: Ticket[];
  status: "idle" | "loading" | "succeeded" | "failed";
  error: string | null;
}

const initialState: TicketState = {
  tickets: [],
  status: "idle",
  error: null,
};

const ticketSlice = createSlice({
  name: "ticket",
  initialState,
  reducers: {
    addTicket: (state, action: PayloadAction<Ticket>) => {
      state.tickets.push(action.payload);
    },

    removeTicket: (state, action: PayloadAction<number>) => {
      state.tickets = state.tickets.filter(
        (_, index) => index !== action.payload
      );
    },

    updateTicket: (
      state,
      action: PayloadAction<{ index: number; ticket: Ticket }>
    ) => {
      const { index, ticket } = action.payload;
      if (state.tickets[index]) {
        state.tickets[index] = ticket;
      }
    },

    resetTickets: (state) => {
      state.tickets = initialState.tickets;
      state.status = "idle";
      state.error = null;
    },
  },
});

export const { addTicket, removeTicket, updateTicket, resetTickets } =
  ticketSlice.actions;
export default ticketSlice.reducer;
