// React libraries
import { FC, useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";

// Third party libraries
import Box from "@mui/material/Box";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import OtpInput from "react-otp-input";
import Button from "@mui/material/Button";
import SendIcon from "@mui/icons-material/Send";
import VerifiedIcon from "@mui/icons-material/Verified";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";

// Utility
import { getItem } from "../../utils/localstorage";
import { usePersistedState } from "../../hooks/usePersistedState";

// Assets
import "./VerifyPageStyles.scss";
import { VerifyPageStyles } from "./VerifyPageStyles";

// Constants
import { labels } from "./Labels";

// Redux related
import { AppDispatch } from "../../redux/store";
import { resendCode } from "../../redux/services/resendService";
import { verifyUser } from "../../redux/services/verifyService";

interface AlertState {
  open: boolean;
  message: string;
  severity: "success" | "error";
}

const VerifyPage: FC = () => {
  const [verified, setVerified] = useState<boolean>(false);
  const [sending, setSending] = useState<boolean>(false);
  const [otp, setOtp] = useState<string>("");
  const email: string =
    useSelector((state: any) => state.registerData.email) || getItem("email");
  const [tooManyEmailsSent, setTooManyEmailsSent] = useState<boolean>(false);
  const [timesEmailSent, setTimesEmailSent] = usePersistedState<number>(
    "timesEmailSent",
    0
  );
  const [alert, setAlert] = useState<AlertState>({
    open: false,
    message: "",
    severity: "success",
  });
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();

  const handleAlertClose = (
    event?: React.SyntheticEvent | Event,
    reason?: string
  ): void => {
    if (reason === "clickaway") {
      return;
    }
    setAlert((prevAlert) => ({ ...prevAlert, open: false }));
  };

  const verify = async (): Promise<void> => {
    if (!otp) {
      setAlert((prevAlert) => ({
        ...prevAlert,
        open: true,
        message: labels.pleaseEnterCode,
        severity: "error",
      }));
      return;
    }

    try {
      const verifyRequest = { email, verificationCode: otp };
      const resultAction = await dispatch(verifyUser(verifyRequest));

      if (verifyUser.fulfilled.match(resultAction)) {
        const successMsg: string = labels.emailVerifiedSuccessfullyRedirecting;
        setVerified((prev) => !prev);
        setAlert((prevAlert) => ({
          ...prevAlert,
          open: true,
          message: successMsg,
          severity: "success",
        }));
        setTimesEmailSent(0);
        setTimeout(() => {
          navigate("/");
        }, 3000);
      } else {
        const errorPayload = resultAction.payload as { message?: string };
        const errorMsg: string =
          errorPayload.message || labels.verificationFailedPleaseTryAgain;
        setAlert((prevAlert) => ({
          ...prevAlert,
          open: true,
          message: errorMsg,
          severity: "error",
        }));
      }
    } catch (error) {
      setAlert((prevAlert) => ({
        ...prevAlert,
        open: true,
        message: labels.anUnexpectedErrorOccured,
        severity: "error",
      }));
    }
  };

  const resend = async (): Promise<void> => {
    if (timesEmailSent > 2) {
      setTooManyEmailsSent((prev) => !prev);
      setAlert((prevAlert) => ({
        ...prevAlert,
        open: true,
        message: labels.tooManyEmailsSentPleaseTryAgainLater,
        severity: "error",
      }));
    } else {
      setSending((prev) => !prev);
      try {
        const resultAction = await dispatch(resendCode(email));
        if (resendCode.fulfilled.match(resultAction)) {
          setTimeout(() => {
            setAlert((prevAlert) => ({
              ...prevAlert,
              open: true,
              message: labels.emailHasBeenResentSuccessfully,
              severity: "success",
            }));
            setSending((prev) => !prev);
          }, 2000);
          setTimesEmailSent((prev) => prev + 1);
        } else {
          const errorPayload = resultAction.payload as { message?: string };
          const errorMsg: string =
            errorPayload.message || labels.resendingFailedPleaseTryAgain;
          setAlert((prevAlert) => ({
            ...prevAlert,
            open: true,
            message: errorMsg,
            severity: "error",
          }));
        }
      } catch (error) {
        setAlert((prevAlert) => ({
          ...prevAlert,
          open: true,
          message: labels.anUnexpectedErrorOccured,
          severity: "error",
        }));
      }
    }
  };

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
          <Button
            onClick={resend}
            disabled={tooManyEmailsSent || verified || sending}
          >
            {sending ? labels.sending : labels.resendCode}
          </Button>
        </Typography>
      </Box>
      <Snackbar
        open={alert.open}
        autoHideDuration={5000}
        onClose={handleAlertClose}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          onClose={handleAlertClose}
          severity={alert.severity}
          sx={{ width: "100%" }}
        >
          {alert.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default VerifyPage;
