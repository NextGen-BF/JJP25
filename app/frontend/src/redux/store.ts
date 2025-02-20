import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import eventReducer from "./slices/eventSlice";
import authReducer from "./slices/authSlice";

export const store = configureStore({
  reducer: {
    users: usersReducer,
    event: eventReducer,
    auth: authReducer
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
