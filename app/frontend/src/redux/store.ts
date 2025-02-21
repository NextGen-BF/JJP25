import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import eventReducer from "./slices/eventSlice";
import registerReducer from "./slices/registerSlice";

export const store = configureStore({
  reducer: {
    users: usersReducer,
    event: eventReducer,
    registerData: registerReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
