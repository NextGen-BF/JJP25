// React libraries
import { FC, useState } from "react";
import { useNavigate } from "react-router-dom";

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
  const [verified, setVerified] = useState(false);
  const [sent, setSent] = useState(false);
  const [otp, setOtp] = useState("");
  const email = "petar.mitov@blankfactor.com"
  const navigate = useNavigate();

  function verify() {
    if (otp.length !== 6) {
      alert("Please enter a valid 6-digit OTP.");
      return;
    }
    fetch("http://localhost:8081/api/v1/auth/verify", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: email,
        verificationCode: otp,
      }),
    })
      .then((response) => response.json())
      .then((data) => {console.log(data); setVerified(true); navigate("")})
      .catch((error) => console.error(error));
  }

  function resendCode() {
    fetch(`http://localhost:8081/api/v1/auth/resend?email=${email}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => response.json())
      .then(() => setSent(true))
      .catch((error) => console.error("Error:", error));
  }

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
          onClick={verify}
          disabled={verified}
        >
          {labels.verify}
        </Button>
        <Typography>
          {labels.youDidntReceiveAnyEmailFromUs}{" "}
          <Button onClick={resendCode} disabled={sent || verified}>
            {sent ? labels.sent : labels.resendCode}
          </Button>
        </Typography>
      </Box>
    </Container>
  );
};

export default RegisterPage;
