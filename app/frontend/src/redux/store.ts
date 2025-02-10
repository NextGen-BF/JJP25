import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import sidebarReducer from "./slices/sidebarSlice"

export const store = configureStore({
  reducer: {
    sidebar: sidebarReducer,
    users: usersReducer,
  },
});

// Export RootState and AppDispatch types
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;