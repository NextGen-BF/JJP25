// React libraries
import { FC, useState } from "react";

// Third party libraries
import Box from "@mui/material/Box";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import OtpInput from "react-otp-input";
import Button from "@mui/material/Button";
import SendIcon from "@mui/icons-material/Send";

// Utility

// Assets
import "./VerifyPageStyles.scss";
import { VerifyPageStyles } from "./VerifyPageStyles";

// Constants
import { labels } from "./Labels";

// Redux related

const RegisterPage: FC = () => {
  const [otp, setOtp] = useState("");
  const email = "example@email.com";

  return (
    <Container sx={VerifyPageStyles.verifyContainer}>
      <Box sx={VerifyPageStyles.verifyBox}>
        <Typography variant="h3" fontWeight="bold" sx={{ mb: 2 }}>
          {labels.verifyYourEmailAddress}
        </Typography>
        <Typography variant="h6">
          {labels.weEmailedYouA6DigitCodeTo} <strong>{email}</strong>
          {". "}
          {labels.enterTheCodeBelowToConfirmYourEmailAddress}
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
        <Button
          variant="contained"
          endIcon={<SendIcon />}
          sx={VerifyPageStyles.verifyButton}
        >
          {labels.verify}
        </Button>
        <Typography>
          {labels.youDidntReceiveAnyEmailFromUs}{" "}
          <strong>{labels.resendCode}</strong>
        </Typography>
      </Box>
    </Container>
  );
};

export default RegisterPage;
