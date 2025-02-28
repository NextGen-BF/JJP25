// React libraries
import { FC, useState } from "react";

// Third party libraries
import Box from "@mui/material/Box";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import OtpInput from "react-otp-input";
import Button from "@mui/material/Button";
import SendIcon from "@mui/icons-material/Send";
import VerifiedIcon from "@mui/icons-material/Verified";

// Utility
import { getItem } from "../../utils/localstorage";
import { usePersistedState } from "../../hooks/usePersistedState";

// Assets
import "./VerifyPageStyles.scss";
import { VerifyPageStyles } from "./VerifyPageStyles";

// Constants
import { labels } from "./Labels";
import { useSelector } from "react-redux";

// Redux related

const RegisterPage: FC = () => {
  const [verified, setVerified] = useState(false);
  const [sending, setSending] = useState(false);
  const [otp, setOtp] = useState("");
  const email =
    useSelector((state: any) => state.registerData.email) || getItem("email");
  const [tooManyEmailsSent, setTooManyEmailsSent] = useState(false);
  const [timesEmailSent, setTimesEmailSent] = usePersistedState<number>("timesEmailSent", 0);

  function verify() {}

  function resendCode() {
    if (timesEmailSent >= 2) {
      setTooManyEmailsSent(true);
    } else {
      console.log("works");
      setSending(true);
      setTimeout(() => {}, 50000);
      // send email
      setSending(false);
      setTimesEmailSent(timesEmailSent + 1);
    }
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
          endIcon={verified ? <VerifiedIcon /> : <SendIcon />}
          sx={VerifyPageStyles.verifyButton}
          onClick={verify}
          disabled={verified}
        >
          {verified ? labels.verified : labels.verify}
        </Button>
        <Typography>
          {labels.youDidntReceiveAnyEmailFromUs}{" "}
          <Button onClick={resendCode} disabled={tooManyEmailsSent || verified}>
            {sending ? labels.sending : labels.resendCode}
          </Button>
        </Typography>
      </Box>
    </Container>
  );
};

export default RegisterPage;
