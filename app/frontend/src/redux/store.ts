import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import eventReducer from "./slices/eventSlice";
import venueReducer from "./slices/venueSlice";
import authReducer from "./slices/authSlice";
import sidebarReducer from "./slices/sidebarSlice"
import ticketReducer from "./slices/ticketSlice";

export const store = configureStore({
  
  reducer: {
    sidebar: sidebarReducer,
    users: usersReducer,
    event: eventReducer,
    venue: venueReducer,
    auth: authReducer,
    ticket: ticketReducer,
  },

  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ["event/updateEvent"],
        ignoredPaths: ["event.event.dates"],
      },
    }),

});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
