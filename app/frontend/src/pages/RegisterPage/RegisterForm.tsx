import {
  Box,
  Button,
  FormControl,
  FormControlLabel,
  FormLabel,
  Radio,
  RadioGroup,
  TextField,
  Typography,
} from "@mui/material";
import { FC } from "react";
import { DemoContainer } from "@mui/x-date-pickers/internals/demo";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { SubmitHandler, useForm, Controller } from "react-hook-form";
import dayjs from "dayjs";
import googleLogo from "../../assets/google-color.png";
import { Link } from "react-router-dom";
import "./RegisterForm.scss";
import { validationErrors } from "./ValidationErrors";
import axios from "axios";

type FormFields = {
  email: string;
  password: string;
  confirmPassword: string;
  role: string;
  username: string;
  firstName: string;
  lastName: string;
  birthDate: string;
};

const RegisterForm: FC = () => {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors, isSubmitting },
    watch,
    setError
  } = useForm<FormFields>();

  const password = watch("password");

  const emailRegex =
    /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)])/i;
  const passwordRegex =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,25}$/;
  const usernameRegex = /^(?=[a-zA-Z]*[a-zA-Z]{4,})[a-zA-Z0-9_-]{4,25}$/;

  const url = "http://localhost:8081/api/v1/auth/register";
  const onSubmit: SubmitHandler<FormFields> = async (data) => {
    try {
      const response = await axios.post(url, data);
      console.log(response.data);
    } catch (error: unknown) {
      if (axios.isAxiosError(error) && error.response) {
        const responseData = error.response.data;
        if (error.response.status === 400 && typeof responseData === "object") {
          Object.keys(responseData).forEach((field) => {
            setError(field as keyof FormFields, {
              type: "server",
              message: responseData[field][0]
            });
          });
        } else if (error.response.status === 409 && responseData?.message) {
          if (responseData.message.includes("@")) {
            setError("email", { type: "server", message: responseData.message });
          } else {
            setError("username", { type: "server", message: responseData.message });
          } 
        }
      } else {
        console.error("Unexpected error:", error);
      }
    }
  };
  

  return (
    <Box
      className="form-box"
      component="form"
      onSubmit={handleSubmit(onSubmit)}
    >
      <Box sx={{ color: "var(--blankfactor-oxford-color)" }}>
        <Box sx={{ fontSize: "2em" }}>Sign up</Box>
        <Box>
          Already have an account?{" "}
          <Link to="/login">
            <strong>Sign in!</strong>
          </Link>
        </Box>
      </Box>
      <Box className="form-content-box">
        <Box className="form-content-item-box">
          <TextField
            label="Email"
            variant="outlined"
            {...register("email", {
              required: validationErrors.email.required,
              pattern: {
                value: emailRegex,
                message: validationErrors.email.invalid,
              },
            })}
          />
          {errors.email && (
            <Box className="error-box">{errors.email.message}</Box>
          )}
          <TextField
            label="Password"
            variant="outlined"
            {...register("password", {
              required: validationErrors.password.required,
              pattern: {
                value: passwordRegex,
                message: validationErrors.password.invalid,
              },
            })}
          />
          {errors.password && (
            <Box className="error-box">{errors.password.message}</Box>
          )}
          <TextField
            label="Confirm password"
            variant="outlined"
            {...register("confirmPassword", {
              required: validationErrors.confirmPassword.required,
              validate: (value) =>
                value == password || validationErrors.confirmPassword.mismatch,
            })}
          />
          {errors.confirmPassword && (
            <Box className="error-box">{errors.confirmPassword.message}</Box>
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
        <Box className="form-content-item-box">
          <TextField
            label="Username"
            variant="outlined"
            {...register("username", {
              required: validationErrors.username.required,
              pattern: {
                value: usernameRegex,
                message: validationErrors.username.invalid,
              },
            })}
          />
          {errors.username && (
            <Box className="error-box">{errors.username.message}</Box>
          )}
          <TextField
            label="First name"
            variant="outlined"
            {...register("firstName", {
              required: validationErrors.firstName.required,
              minLength: {
                value: 2,
                message: validationErrors.firstName.minLength,
              },
              maxLength: {
                value: 20,
                message: validationErrors.firstName.maxLength,
              },
            })}
          />
          {errors.firstName && (
            <Box className="error-box">{errors.firstName.message}</Box>
          )}
          <TextField
            label="Last name"
            variant="outlined"
            {...register("lastName", {
              required: validationErrors.lastName.required,
              minLength: {
                value: 2,
                message: validationErrors.lastName.minLength,
              },
              maxLength: {
                value: 20,
                message: validationErrors.lastName.maxLength,
              },
            })}
          />
          {errors.lastName && (
            <Box className="error-box">{errors.lastName.message}</Box>
          )}
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DemoContainer components={["DatePicker"]}>
              <Controller
                name="birthDate"
                control={control}
                rules={{ required: validationErrors.birthdate.required }}
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
                      <Box className="error-box" sx={{ mt: 2 }}>
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
      <Button
        sx={{
          bgcolor: "#080731",
        }}
        variant="contained"
        type="submit"
        disabled={isSubmitting}
      >
        {isSubmitting ? "Loading..." : "Sign up"}
      </Button>
      <Box className="or-box">
        <Box className="or-hr" />
        <Typography variant="body2" color="textSecondary">
          or
        </Typography>
        <Box className="or-hr" />
      </Box>
      <Button
        variant="contained"
        startIcon={
          <img
            src={googleLogo}
            alt="Google"
            style={{ width: 20, height: 20 }}
          />
        }
        sx={{
          backgroundColor: "white",
          color: "black",
          "&:hover": {
            backgroundColor: "#f5f5f5",
          },
        }}
      >
        Sign up with Google
      </Button>
    </Box>
  );
};

export default RegisterForm;
