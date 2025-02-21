import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import axios from "axios";
import { RootState } from "../store";
import { ForgotPasswordConstants, ResetPasswordConstants } from "../../constants/AuthenticationConstants";

interface PasswordResetState {
  loading: boolean;
  error: string | null;
  success: boolean;
  message: string | null;
}

const initialState: PasswordResetState = {
  loading: false,
  error: null,
  success: false,
  message: null,
};

export const forgotPassword = createAsyncThunk(
  "passwordReset/forgotPassword",
  async (email: string, { rejectWithValue }) => {
    try {
      const response = await axios.post(
        import.meta.env.VITE_API_URL + "/api/v1/auth/forgot-password",
        { email }
      );
      return response.data.message || ForgotPasswordConstants.SUCCESS_MESSAGE;
    } catch (err: any) {
      return rejectWithValue(
        err.response?.data?.message || ForgotPasswordConstants.ERROR_GENERIC
      );
    }
  }
);

export const resetPassword = createAsyncThunk(
  "passwordReset/resetPassword",
  async (
    data: { token: string; newPassword: string; confirmPassword: string },
    { rejectWithValue, dispatch }
  ) => {
    try {
      await axios.post(
        import.meta.env.VITE_API_URL + "/api/v1/auth/reset-password",
        data
      );
      return true;
    } catch (err: any) {
      if (err.response?.status === 400) {
        const errors = err.response.data.errors;
        if (errors.newPassword) {
          dispatch(showErrorMessage(errors.newPassword));
        } else if (errors.confirmPassword) {
          dispatch(showErrorMessage(errors.confirmPassword));
        } else {
        }
      }
      return rejectWithValue(
        err.response?.data?.message || ResetPasswordConstants.ERROR_RESETTING_PASSWORD
      );
    }
  }
);

const passwordResetSlice = createSlice({
  name: "passwordReset",
  initialState,
  reducers: {
    clearState: (state) => {
      state.loading = false;
      state.error = null;
      state.success = false;
      state.message = null;
    },
    showErrorMessage: (state, action) => {
      state.error = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(forgotPassword.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.message = null;
      })
      .addCase(forgotPassword.fulfilled, (state, action) => {
        state.loading = false;
        state.success = true;
        state.message = action.payload;
      })
      .addCase(forgotPassword.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(resetPassword.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.message = null;
      })
      .addCase(resetPassword.fulfilled, (state) => {
        state.loading = false;
        state.success = true;
        state.error = null;
      })
      .addCase(resetPassword.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { clearState, showErrorMessage } = passwordResetSlice.actions;
export const selectPasswordReset = (state: RootState) => state.passwordReset;
export default passwordResetSlice.reducer;
