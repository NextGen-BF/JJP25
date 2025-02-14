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
  const {
    register,
    handleSubmit,
    control,
    formState: { errors, isSubmitting },
  } = useForm<FormFields>();

  const onSubmit: SubmitHandler<FormFields> = async (data) => {
    await new Promise((resolve) => setTimeout(resolve, 1000));
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
                {...register("email", {
                  required: true,
                  pattern: /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)])/i
                })}
              />
              <TextField
                label="Password"
                variant="outlined"
                {...register("password", {
                  required: true,
                  pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,25}$/
                })}
              />
              <TextField
                label="Confirm password"
                variant="outlined"
                {...register("confirmPassword", {
                  required: true,
                  pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,25}$/
                })}
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
                {...register("username", {
                  required: true,
                  pattern: /^(?=[a-zA-Z]*[a-zA-Z]{4,})[a-zA-Z0-9_-]{4,25}$/
                })}
              />
              <TextField
                label="First name"
                variant="outlined"
                {...register("firstName", {
                  required: true,
                  minLength: 2,
                  maxLength: 20
                })}
              />
              <TextField
                label="Last name"
                variant="outlined"
                {...register("lastName", {
                  required: true,
                  minLength: 2,
                  maxLength: 20
                })}
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
          <Button variant="contained" type="submit" disabled={isSubmitting}>
            {isSubmitting ? "Loading..." : "Sign up"}
          </Button>
        </form>
      </Box>
    </Container>
  );
};

export default RegisterPage;
