import { configureStore } from "@reduxjs/toolkit";
import usersReducer from "./slices/usersSlice";
import eventReducer from "./slices/eventSlice";
import authReducer from "./slices/authSlice";
import passwordResetReducer from "./slices/passwordResetSlice"; 


export const store = configureStore({
  reducer: {
    users: usersReducer,
    event: eventReducer,
    auth: authReducer,
    passwordReset: passwordResetReducer
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
