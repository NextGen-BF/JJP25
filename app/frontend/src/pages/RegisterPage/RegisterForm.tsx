// MUI components
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import FormControlLabel from "@mui/material/FormControlLabel";
import FormLabel from "@mui/material/FormLabel";
import Radio from "@mui/material/Radio";
import RadioGroup from "@mui/material/RadioGroup";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";

// React libraries
import { FC, useEffect } from "react";
import { useForm, Controller, SubmitHandler } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";

// Other libraries
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
dayjs.extend(utc);

// Styling
import googleLogo from "../../assets/google-color.png";
import "./RegisterForm.scss";

// Constants
import { validationErrors } from "./ValidationErrors";
import { regex } from "./Regex";
import { label } from "./Labels";

// Redux related
import { AppDispatch } from "../../redux/store";
import { RootState } from "../../redux/store";
import { registerUser } from "../../redux/services/registerService";
import { resetState } from "../../redux/slices/registerSlice";

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
    formState: { errors },
    getValues,
    setError,
  } = useForm<FormFields>();

  const navigate = useNavigate();

  const dispatch = useDispatch<AppDispatch>();
  const { loading, success } = useSelector(
    (state: RootState) => state.registerData
  );

  const onSubmit: SubmitHandler<FormFields> = async (data) => {
    try {
      const resultAction = await dispatch(registerUser(data));

      if (registerUser.rejected.match(resultAction)) {
        const errorResponse = resultAction.payload as { message?: string };
        const errorMessage =
          errorResponse?.message || "An unexpected error occurred.";
        if (errorMessage.includes("@")) {
          setError("email", { type: "server", message: errorMessage });
        } else {
          setError("username", { type: "server", message: errorMessage });
        }
      }
    } catch (error) {
      console.error("Unexpected error:", error);
    }
  };

  useEffect(() => {
    if (success) {
      navigate("/verify");
      dispatch(resetState());
    }
  }, [success, navigate, dispatch]);

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
            error={!!errors.email}
            label="Email"
            variant="outlined"
            helperText={errors.email ? errors.email.message : " "}
            {...register("email", {
              required: validationErrors.email.required,
              pattern: {
                value: regex.email,
                message: validationErrors.email.invalid,
              },
            })}
          />
          <TextField
            error={!!errors.password}
            label="Password"
            variant="outlined"
            type="password"
            helperText={errors.password ? errors.password.message : " "}
            {...register("password", {
              required: validationErrors.password.required,
              pattern: {
                value: regex.password,
                message: validationErrors.password.invalid,
              },
            })}
          />
          <TextField
            error={!!errors.confirmPassword}
            label="Confirm password"
            variant="outlined"
            type="password"
            helperText={
              errors.confirmPassword ? errors.confirmPassword.message : " "
            }
            {...register("confirmPassword", {
              required: validationErrors.confirmPassword.required,
              validate: (value) =>
                value == getValues("password") ||
                validationErrors.confirmPassword.mismatch,
            })}
          />
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
            error={!!errors.username}
            label="Username"
            variant="outlined"
            helperText={errors.username ? errors.username.message : " "}
            {...register("username", {
              required: validationErrors.username.required,
              pattern: {
                value: regex.username,
                message: validationErrors.username.invalid,
              },
            })}
          />
          <TextField
            error={!!errors.firstName}
            label="First name"
            variant="outlined"
            helperText={errors.firstName ? errors.firstName.message : " "}
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
          <TextField
            error={!!errors.lastName}
            label="Last name"
            variant="outlined"
            helperText={errors.lastName ? errors.lastName.message : " "}
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
          <Controller
            name="birthDate"
            control={control}
            rules={{ required: validationErrors.birthdate.required }}
            render={({ field, fieldState: { error } }) => (
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DatePicker
                  label="Birthdate"
                  value={field.value ? dayjs(field.value) : null}
                  onChange={(date) =>
                    field.onChange(
                      date ? date.utc().startOf("day").toISOString() : ""
                    )
                  }
                  slotProps={{
                    textField: {
                      variant: "outlined",
                      error: !!error,
                      helperText: error ? error.message : " ",
                    },
                  }}
                />
              </LocalizationProvider>
            )}
          />
        </Box>
      </Box>
      <Button
        sx={{
          bgcolor: "var(--blankfactor-oxford-color)",
        }}
        variant="contained"
        type="submit"
        disabled={loading}
      >
        {loading ? label.loading : label.singUp}
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
