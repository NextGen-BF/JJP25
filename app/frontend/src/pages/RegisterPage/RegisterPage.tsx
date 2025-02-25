// MUI components
import { Box, Container } from "@mui/material";

// React
import { FC } from "react";

// Components and Styling
import "./RegisterPage.scss";
import RegisterForm from "./RegisterForm";

const RegisterPage: FC = () => {
  return (
    <Container className="register-container">
      <Box className="register-box">
        <Box className="image-box" />
        <RegisterForm />
      </Box>
    </Container>
  );
};

export default RegisterPage;
