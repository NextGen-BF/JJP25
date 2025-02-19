import { createSlice } from "@reduxjs/toolkit";
import { createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";

export const registerUser = createAsyncThunk(
  "auth/registerUser",
  async (userData: Record<string, any>, { rejectWithValue }) => {
    try {
      const response = await axios.post(
        import.meta.env.VITE_SIGN_UP_URL,
        userData
      );
      return response.data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        return rejectWithValue(error.response.data);
      }
      return rejectWithValue("Unexpected error occurred");
    }
  }
);

const authSlice = createSlice({
  name: "auth",
  initialState: {},
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(registerUser.rejected, (state, action) => {
      console.error("Registration failed:", action.payload);
    });
  },
});

export default authSlice.reducer;
