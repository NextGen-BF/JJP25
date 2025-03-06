import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { UserRole } from "../../components/SelectRoleComponent/SelectRole";
import { GoogleUser } from "../../fireabase_context/AuthContext";

interface UserState {
  user: GoogleUser | null;
  role: UserRole;
  loading: boolean;
}

const initialState: UserState = {
  user: null,
  role: null,
  loading: false,
};

const googleAuthSlice = createSlice({
  name: "googleAuth",
  initialState,
  reducers: {
    setUser: (state, action: PayloadAction<GoogleUser | null>) => {
      state.user = action.payload;
    },
    setRole: (state, action: PayloadAction<UserRole>) => {
      state.role = action.payload;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    signOutUser: (state) => {
      state.user = null;
      state.role = null;
    },
  },
});

export const { setUser, setRole, setLoading, signOutUser } = googleAuthSlice.actions;
export default googleAuthSlice.reducer;
