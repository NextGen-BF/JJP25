// React libraries
import { FC, useState } from "react";

// Third party libraries
import Box from "@mui/material/Box";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import OtpInput from "react-otp-input";

// Utility

// Assets
import "./VerifyPage.scss";

// Constants
import { label } from "./Labels";

// Redux related

const RegisterPage: FC = () => {
  const [otp, setOtp] = useState("");
  const email = "example@email.com";

  return (
    <Container className="verify-container">
      <Box className="verify-box">
          <Typography variant="h3" fontWeight="bold" sx={{ mb: 2 }}>
            {label.verifyYourEmailAddress}
          </Typography>
          <Typography variant="h6">
            {label.weEmailedYouA6DigitCodeTo} <strong>{email}</strong>{". "}
            {label.enterTheCodeBelowToConfirmYourEmailAddress}
          </Typography>
        <OtpInput
          numInputs={6}
          value={otp}
          onChange={setOtp}
          inputStyle="otp-input"
          skipDefaultStyles={true}
          shouldAutoFocus={true}
          renderInput={(props) => <input {...props} />}
        />
      </Box>
    </Container>
  );
};

export default RegisterPage;
