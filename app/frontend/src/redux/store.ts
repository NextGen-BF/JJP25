import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import eventReducer from "./slices/eventSlice";

export const store = configureStore({
  reducer: {
    users: usersReducer,
    event: eventReducer
  },
});

// Export RootState and AppDispatch types
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
