import { createSlice } from "@reduxjs/toolkit";
import { registerUser } from "../services/authService";

type AuthState = {
  email: string | null;
  username: string | null;
  firstName: string | null;
  lastName: string | null;
  loading: boolean;
  error: string | null;
  success: boolean;
};

const initialState: AuthState = {
  email: null,
  username: null,
  firstName: null,
  lastName: null,
  loading: false,
  error: null,
  success: false,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    resetState: (state) => {
      state.loading = false;
      state.error = null;
      state.success = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(registerUser.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.success = false;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.loading = false;
        state.error = null;
        state.success = true;
        const { email, username, firstName, lastName } = action.payload;
        state.email = email;
        state.username = username;
        state.firstName = firstName;
        state.lastName = lastName;
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
        state.success = false;
      });
  },
});

export const { resetState } = authSlice.actions;
export default authSlice.reducer;
