import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { RootState } from '../store'; 

interface FormValues {
  username: string;
  password: string;
  rememberMe: boolean;
}

interface AuthState {
  loading: boolean;
  error: string | null;
  token: string | null;
}

const initialState: AuthState = {
  loading: false,
  error: null,
  token: null,
};

interface LoginError {
  message: string;
}

export const loginUser = createAsyncThunk(
  'auth/login',
  async (formValues: FormValues, { rejectWithValue }) => {
    try {
      const response = await axios.post(
        import.meta.env.VITE_API_URL + '/api/v1/auth/login',
        {
          loginIdentifier: formValues.username,
          password: formValues.password,
        }
      );
      const { token } = response.data;
      localStorage.setItem('token', token);
      return token;
    } catch (error: any) {
      let errorMessage: string | undefined = 'An unexpected error occurred.';
      if (axios.isAxiosError(error)) {
        if (error.response) {
          errorMessage =
            (error.response.data as LoginError)?.message ||
            error.response.statusText;
        } else if (error.request) {
          errorMessage = 'No response received from server.';
        } else {
          errorMessage = error.message;
        }
      } else if (error instanceof Error) {
        errorMessage = error.message;
      }

      return rejectWithValue(errorMessage);
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout: (state) => {
      localStorage.removeItem('token');
      state.token = null;
      state.error = null;
    },
    clearError: (state) => {
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder.addCase(loginUser.pending, (state) => {
      state.loading = true;
      state.error = null;
    });
    builder.addCase(loginUser.fulfilled, (state, action) => {
      state.loading = false;
      state.token = action.payload;
    });
    builder.addCase(loginUser.rejected, (state, action) => {
      state.loading = false;
      state.error = action.payload as string;
      state.token = null;
    });
  },
});

export const { logout, clearError } = authSlice.actions;
export const selectAuth = (state: RootState) => state.auth;
export default authSlice.reducer;