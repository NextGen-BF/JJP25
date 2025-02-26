import { createSlice } from "@reduxjs/toolkit";

const sidebarSlice = createSlice({
    name: "sidebar",
    initialState: {
        isOpen: false
    },
    reducers: {
        toggleSidebarStatus: (state) => {
            state.isOpen = !state.isOpen;
        }
    }
});

export const { toggleSidebarStatus: toggleSidebarStatus } = sidebarSlice.actions

export default sidebarSlice.reducer;