export const SelectRoleConstants = {
  URLs: {
    DASHBOARD_URL: "/dashboard",
    LOGIN_URL: "/login",
    SELECT_ROLE_URL: "/select-role",
  },

  DATABASE: {
    USERS_COLLECTION: "users",
  },

  ROLES: {
    ATTENDEE: "ATTENDEE",
    ORGANIZER: "ORGANIZER",
  },

  SUCCESS_MESSAGES: {
    ROLE_ASSIGNED_SUCCESSFULLY: "Role assigned successfully!",
    WELCOME_MESSAGE: (name: string | null | undefined) =>
      `Hello, ${name}! Choose your role to proceed.`,
    WELCOME_BACK_MESSAGE: (name: string | null) => `Welcome back, ${name}`,
  },

  ERROR_MESSAGES: {
    ERROR_OCCURED: "An error occured. Please try again.",
    ERROR_OCCRED_SIGN_OUT:
      "An error occured during sign out. Please try again.",
  },

  LABELS: {
    CONFIRM_ROLE: "Confirm role",
  },
};
