import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import eventReducer from "./slices/eventSlice";
import registerReducer from "./slices/registerSlice";
import venueReducer from "./slices/venueSlice";
import authReducer from "./slices/authSlice";

export const store = configureStore({
  reducer: {
    users: usersReducer,
    event: eventReducer,
    venue: venueReducer,
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
