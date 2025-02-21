export const LoginPageConstants = {
    LOGIN_TEXT: "Login",
    EMAIL_USERNAME_LABEL: "Email/Username",
    PASSWORD_LABEL: "Password",
    REMEMBER_ME_LABEL: "Remember Me",
    LOGIN_BUTTON_TEXT: "Login",
    OR_SEPARATOR: "or",
    GMAIL_LOGIN_TEXT: "Login with Gmail",
    SIGNUP_PROMPT: "Don't have an account?",
    SIGNUP_TEXT: "Sign up!",
    PLACEHOLDER_FOR_IDENTIFIER: "Enter your email or username",
    PLACEHOLDER_FOR_PASSWORD: "Enter your password",
    LOGGING_IN: "Logging in...",
    ERROR_MISSING_IDENTIFIER: "Please enter your email/username",
    ERROR_MISSING_PASSWORD: "Please enter your password",
    LOGIN_FAILED: "Login failed: ",
    FORGOT_PASSWORD_PROMPT: "Forgot Password?"
}

export const ResetPasswordConstants = {
    PASSWORD_REGEX: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,25}$/,
    RESET_PASSWORD_TITLE: "Reset Your Password",
    NEW_PASSWORD_LABEL: "New Password",
    CONFIRM_PASSWORD_LABEL: "Confirm New Password",
    NEW_PASSWORD_PLACEHOLDER: "Enter your new password",
    CONFIRM_PASSWORD_PLACEHOLDER: "Confirm your new password",
    RESET_BUTTON_TEXT: "Reset Password",
    RESETTING_BUTTON_TEXT: "Resetting...",
    ERROR_MISSING_NEW_PASSWORD: "Please enter a new password.",
    ERROR_MISSING_CONFIRM_PASSWORD: "Please confirm your new password.",
    ERROR_PASSWORDS_DONT_MATCH: "Passwords do not match.",
    ERROR_INVALID_PASSWORD_FORMAT:
      "Password must be 8-25 characters long, include an uppercase letter, a lowercase letter, a number, and a special character.",
    SUCCESS_MESSAGE: "Password reset successfully! Redirecting to login...",
    ERROR_RESETTING_PASSWORD: "Error resetting password: ",
  };
  
  export const ForgotPasswordConstants = {
    FORGOT_PASSWORD_TITLE: "Forgot Password",
    EMAIL_LABEL: "Enter your email address",
    EMAIL_PLACEHOLDER: "Your email",
    SEND_RESET_LINK_TEXT: "Send Reset Link",
    SENDING_TEXT: "Sending...",
    ERROR_MISSING_EMAIL: "Please enter your email.",
    SUCCESS_MESSAGE: "An email with a reset link has been sent.",
    ERROR_GENERIC: "An error occurred. Please try again.",
    ERROR_SENDING_EMAIL: "Error sending reset email: "
  };