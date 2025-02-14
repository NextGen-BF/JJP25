import {
  Box,
  Container,
  FormControl,
  FormControlLabel,
  FormLabel,
  Radio,
  RadioGroup,
  TextField,
} from "@mui/material";
import { FC } from "react";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";

const RegisterPage: FC = () => {
  return (
    <Container>
      <Box>
        {/* left side */}
        {/* <Box>
          <img src={registerImage} alt="register-image" />
        </Box> */}
        {/* right side */}
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            flexWrap: "wrap",
            gap: "1em",
          }}
        >
          {/* left side */}
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: "1em",
            }}
          >
            <TextField label="Email" variant="outlined" />
            <TextField label="Password" variant="outlined" />
            <TextField label="Confirm password" variant="outlined" />
            <FormControl>
              <FormLabel>Role</FormLabel>
              <RadioGroup defaultValue="attendee" name="radio-buttons-group">
                <FormControlLabel
                  value="organiser"
                  control={<Radio />}
                  label="Organiser"
                />
                <FormControlLabel
                  value="attendee"
                  control={<Radio />}
                  label="Attendee"
                />
              </RadioGroup>
            </FormControl>
          </Box>
          {/* right side */}
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: "1em",
            }}
          >
            <TextField
              id="outlined-basic"
              label="Username"
              variant="outlined"
            />
            <TextField
              id="outlined-basic"
              label="First name"
              variant="outlined"
            />
            <TextField
              id="outlined-basic"
              label="Last name"
              variant="outlined"
            />
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DemoContainer components={["DatePicker"]}>
                <DatePicker label="Birthdate" />
              </DemoContainer>
            </LocalizationProvider>
          </Box>
        </Box>
      </Box>
    </Container>
  );
};

export default RegisterPage;
