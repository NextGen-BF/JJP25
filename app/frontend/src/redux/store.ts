import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import eventReducer from "./slices/eventSlice";
import sidebarReducer from "./slices/sidebarSlice"

export const store = configureStore({
  
  reducer: {
    sidebar: sidebarReducer,
    users: usersReducer,
    event: eventReducer
  },

});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;