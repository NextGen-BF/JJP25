import { createSlice } from "@reduxjs/toolkit";
import { registerUser } from "../services/registerService";
import { setItem } from "../../utils/localstorage";

type userDataState = {
  email: string | null;
  username: string | null;
  firstName: string | null;
  lastName: string | null;
  loading: boolean;
  success: boolean;
};

const initialState: userDataState = {
  email: null,
  username: null,
  firstName: null,
  lastName: null,
  loading: false,
  success: false,
};

const registerSlice = createSlice({
  name: "registerData",
  initialState,
  reducers: {
    resetState: (state) => {
      state.loading = false;
      state.success = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(registerUser.pending, (state) => {
        state.loading = true;
        state.success = false;
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.loading = false; 
        state.success = true;
        const { email, username, firstName, lastName } = action.payload;
        setItem("email", email);
        state.email = email;
        state.username = username;
        state.firstName = firstName;
        state.lastName = lastName;
      })
      .addCase(registerUser.rejected, (state) => {
        state.loading = false;
        state.success = false;
      });
  },
});

export const { resetState } = registerSlice.actions;
export default registerSlice.reducer;
