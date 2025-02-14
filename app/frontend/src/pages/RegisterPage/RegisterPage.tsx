import {
  Box,
  Button,
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
import { SubmitHandler, useForm, Controller } from "react-hook-form";
import dayjs from "dayjs";

type FormFields = {
  email: string;
  password: string;
  confirmPassword: string;
  role: string;
  username: string;
  firstName: string;
  lastName: string;
  birthdate: string;
};

const RegisterPage: FC = () => {
  const { register, handleSubmit, control } = useForm<FormFields>();

  const onSubmit: SubmitHandler<FormFields> = (data) => {
    console.log(data);
  };

  return (
    <Container>
      <Box>
        {/* left side */}
        {/* <Box>
          <img src={registerImage} alt="register-image" />
        </Box> */}
        {/* right side */}
        <form onSubmit={handleSubmit(onSubmit)}>
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              flexWrap: "wrap",
              gap: "2em",
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
              <TextField
                label="Email"
                variant="outlined"
                {...register("email")}
              />
              <TextField
                label="Password"
                variant="outlined"
                {...register("password")}
              />
              <TextField
                label="Confirm password"
                variant="outlined"
                {...register("confirmPassword")}
              />
              <FormControl>
                <FormLabel>Role</FormLabel>
                <RadioGroup defaultValue="attendee" name="radio-buttons-group">
                  <FormControlLabel
                    value="organiser"
                    control={<Radio />}
                    label="Organiser"
                    {...register("role")}
                  />
                  <FormControlLabel
                    value="attendee"
                    control={<Radio />}
                    label="Attendee"
                    {...register("role")}
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
                label="Username"
                variant="outlined"
                {...register("username")}
              />
              <TextField
                label="First name"
                variant="outlined"
                {...register("firstName")}
              />
              <TextField
                label="Last name"
                variant="outlined"
                {...register("lastName")}
              />
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DemoContainer components={["DatePicker"]}>
                  <Controller
                    name="birthdate"
                    control={control}
                    defaultValue=""
                    render={({ field }) => (
                      <DatePicker
                        label="Birthdate"
                        value={field.value ? dayjs(field.value) : null}
                        onChange={(date) =>
                          field.onChange(date ? date.toISOString() : "")
                        }
                        slotProps={{ textField: { variant: "outlined" } }}
                      />
                    )}
                  />
                </DemoContainer>
              </LocalizationProvider>
            </Box>
          </Box>
          <Button variant="contained" type="submit">
            Sign up
          </Button>
        </form>
      </Box>
    </Container>
  );
};

export default RegisterPage;
