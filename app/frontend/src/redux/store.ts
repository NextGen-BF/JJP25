import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import eventReducer from "./slices/eventSlice";
import authReducer from "./slices/authSlice";
import registerReducer from "./slices/registerSlice";

export const store = configureStore({
  reducer: {
    users: usersReducer,
    event: eventReducer,
    auth: authReducer,
    registerData: registerReducer,
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
