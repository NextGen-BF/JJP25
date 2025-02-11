import { createAsyncThunk } from "@reduxjs/toolkit";

export interface User {
  id: number;
  name: string;
  email: string;
}

export interface UsersState {
  users: User[];
  status: "idle" | "loading" | "succeeded" | "failed";
  error: string | null;
}

export const initialState: UsersState = {
  users: [],
  status: "idle",
  error: null,
};

const mockUsers: User[] = [
  { id: 1, name: "John Doe", email: "john@example.com" },
  { id: 2, name: "Jane Doe", email: "jane@example.com" },
];

export const fetchUsers = createAsyncThunk("users/fetchUsers", async () => {
  return new Promise<User[]>((resolve) => {
    setTimeout(() => resolve(mockUsers), 1000);
  });
});