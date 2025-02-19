import {
  Box,
  Button,
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
import { Link, useNavigate } from "react-router-dom";
import "./RegisterForm.scss";
import { validationErrors } from "./ValidationErrors";
import { regex } from "./Regex";
import { label } from "./Labels"
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
    setError,
    getValues,
  } = useForm<FormFields>();

  const navigate = useNavigate();

  // TODO
  const url = import.meta.env.VITE_SIGN_UP_URL;
  const onSubmit: SubmitHandler<FormFields> = async (data) => {
    try {
      const response = await axios.post(url, data);
      if (response.status === 200) {
        navigate("/verify");
      }
    } catch (error: unknown) {
      if (axios.isAxiosError(error) && error.response) {
        const responseData = error.response.data;
        if (error.response.status === 400 && typeof responseData === "object") {
          Object.keys(responseData).forEach((field) => {
            setError(field as keyof FormFields, {
              type: "server",
              message: responseData[field][0],
            });
          });
        } else if (error.response.status === 409 && responseData?.message) {
          if (responseData.message.includes("@")) {
            setError("email", {
              type: "server",
              message: responseData.message,
            });
          } else {
            setError("username", {
              type: "server",
              message: responseData.message,
            });
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
        <Box sx={{ fontSize: "2em" }}>{label.singUp}</Box>
        <Box>
          {label.alreadyHaveAnAccount}
          <Link to="/login">
            <strong>{label.signIn}</strong>
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
                value: regex.email,
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
            type="password"
            {...register("password", {
              required: validationErrors.password.required,
              pattern: {
                value: regex.password,
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
            type="password"
            {...register("confirmPassword", {
              required: validationErrors.confirmPassword.required,
              validate: (value) =>
                value == getValues("password") ||
                validationErrors.confirmPassword.mismatch,
            })}
          />
          {errors.confirmPassword && (
            <Box className="error-box">{errors.confirmPassword.message}</Box>
          )}
          <FormLabel>Role</FormLabel>
          <Controller
            name="role"
            control={control}
            defaultValue="attendee"
            render={({ field }) => (
              <RadioGroup {...field}>
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
            )}
          />
        </Box>
        <Box className="form-content-item-box">
          <TextField
            label="Username"
            variant="outlined"
            {...register("username", {
              required: validationErrors.username.required,
              pattern: {
                value: regex.username,
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
          bgcolor: "var(--blankfactor-oxford-color)",
        }}
        variant="contained"
        type="submit"
        disabled={isSubmitting}
      >
        {isSubmitting ? label.loading : label.singUp}
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
          backgroundColor: "var(--white-color)",
          color: "var(--black-color)",
          "&:hover": {
            backgroundColor: "var(--blankfactor-gray-3-color)",
          },
        }}
      >
        {label.signUpWithGoogle}
      </Button>
    </Box>
  );
};

export default RegisterForm;