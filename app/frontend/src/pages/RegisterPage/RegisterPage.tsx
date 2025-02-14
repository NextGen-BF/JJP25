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
    <Container
      sx={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "97vh",
      }}
    >
      <Box
        sx={{
          display: "flex",
        }}
      >
        {/* left side */}
        <Box></Box>
        {/* right side */}
        <Box
          sx={{
            flex: "1",
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            gap: "1em",
            padding: "1em",
            boxShadow: "rgba(149, 157, 165, 0.2) 0px 8px 24px",
            borderRadius: "5px",
          }}
          component="form"
          onSubmit={handleSubmit(onSubmit)}
        >
          <Box>
            <Box sx={{ fontSize: "2em" }}>Sign up</Box>
            <a href="#">Already have an account? Sign in!</a>
          </Box>
          <Box
            sx={{
              display: "flex",
              justifyContent: "center",
              flexWrap: "wrap",
              gap: "2em",
              padding: "1em",
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
                  required: "Email is required.",
                  pattern: {
                    value:
                      /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)])/i,
                    message: "Please, provide a valid email.",
                  },
                })}
              />
              {errors.email && (
                <Box sx={{ color: "#e53935", fontSize: ".9em" }}>
                  {errors.email.message}
                </Box>
              )}
              <TextField
                label="Password"
                variant="outlined"
                {...register("password", {
                  required: "Password is required.",
                  pattern: {
                    value:
                      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,25}$/,
                    message: "Please, provide a valid password.",
                  },
                })}
              />
              {errors.password && (
                <Box sx={{ color: "#e53935", fontSize: ".9em" }}>
                  {errors.password.message}
                </Box>
              )}
              <TextField
                label="Confirm password"
                variant="outlined"
                {...register("confirmPassword", {
                  required: "Confirm password is required.",
                  pattern: {
                    value:
                      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,25}$/,
                    message: "Please, provide a valid password.",
                  },
                })}
              />
              {errors.confirmPassword && (
                <Box sx={{ color: "#e53935", fontSize: ".9em" }}>
                  {errors.confirmPassword.message}
                </Box>
              )}
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
                  required: "Username is required.",
                  pattern: {
                    value: /^(?=[a-zA-Z]*[a-zA-Z]{4,})[a-zA-Z0-9_-]{4,25}$/,
                    message: "Please, provide a valid username.",
                  },
                })}
              />
              {errors.username && (
                <Box sx={{ color: "#e53935", fontSize: ".9em" }}>
                  {errors.username.message}
                </Box>
              )}
              <TextField
                label="First name"
                variant="outlined"
                {...register("firstName", {
                  required: "First name is required.",
                  minLength: {
                    value: 2,
                    message: "First name must be at least 2 characters long.",
                  },
                  maxLength: {
                    value: 20,
                    message:
                      "First name must not contain more than 20 characters.",
                  },
                })}
              />
              {errors.firstName && (
                <Box sx={{ color: "#e53935", fontSize: ".9em" }}>
                  {errors.firstName.message}
                </Box>
              )}
              <TextField
                label="Last name"
                variant="outlined"
                {...register("lastName", {
                  required: "Last name is required.",
                  minLength: {
                    value: 2,
                    message: "Last name must be at least 2 characters long.",
                  },
                  maxLength: {
                    value: 20,
                    message:
                      "Last name must not contain more than 20 characters.",
                  },
                })}
              />
              {errors.lastName && (
                <Box sx={{ color: "#e53935", fontSize: ".9em" }}>
                  {errors.lastName.message}
                </Box>
              )}
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DemoContainer components={["DatePicker"]}>
                  <Controller
                    name="birthdate"
                    control={control}
                    rules={{ required: "Birthdate is required." }} // Validation rule
                    render={({ field, fieldState: { error } }) => (
                      <Box display="flex" flexDirection="column">
                        <DatePicker
                          label="Birthdate"
                          value={field.value ? dayjs(field.value) : null}
                          onChange={(date) =>
                            field.onChange(date ? date.toISOString() : "")
                          }
                          slotProps={{ textField: { variant: "outlined" } }}
                        />
                        {error && (
                          <Box
                            sx={{
                              color: "#e53935",
                              fontSize: ".9em",
                              mt: "1em",
                            }}
                          >
                            {error.message}
                          </Box>
                        )}
                      </Box>
                    )}
                  />
                </DemoContainer>
              </LocalizationProvider>
            </Box>
          </Box>
          <Button variant="contained" type="submit" disabled={isSubmitting}>
            {isSubmitting ? "Loading..." : "Sign up"}
          </Button>
          <Button variant="contained" type="submit" disabled={isSubmitting}>
            {isSubmitting ? "Loading..." : "Sign up with Google"}
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default RegisterPage;
