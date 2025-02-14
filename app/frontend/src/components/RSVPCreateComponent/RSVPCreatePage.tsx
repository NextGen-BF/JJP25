import { FC } from "react";
import {
  Button,
  Typography,
  Box,
  useTheme,
  useMediaQuery,
} from "@mui/material";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { Dayjs } from "dayjs";
import { Controller, SubmitHandler, useForm } from "react-hook-form";
import FormAutoComplete from "../FormAutoComplete/FormAutoComplete";
import { RSVPCreateComponentStyles } from "./RSVPCreatePageStyles";

interface RSVPFields {
  username: string;
  event: string;
  venue?: string;
  date?: string;
  expirationDate: string;
}

const RSVPCreateComponent: FC = () => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<RSVPFields>();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const onSubmit: SubmitHandler<RSVPFields> = async (data) => {
    console.log("Form submitted!", data);

    try {
      await new Promise((resolve) => setTimeout(resolve, 2000));
      console.log("API response: success!");
    } catch (error) {
      console.error("Submission error:", error);
    }
  };

  const users = ["User1", "User2", "Admin1"];
  const events = ["Event1", "Event2"];
  const venues = ["Venue1", "Venue2"];
  const dates = ["Date1", "Date2"];
  const allowedDate: Date = new Date(2025, 1, 14);

  const isNotTheSameDate = (date: Dayjs, day: Date): boolean => {
    return (
      date.date() != day.getDate() ||
      date.month() != day.getMonth() ||
      date.year() != day.getFullYear()
    );
  };

  const isValidDate = (date: Dayjs) => {
    return isNotTheSameDate(date, allowedDate);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        sx={{
          ...RSVPCreateComponentStyles.fieldBoxStyles,
          height: isMobile ? "calc(100vh - 80px - 64px)" : "680px",
          width: isMobile ? "100%" : "900px",
        }}
      >
        <Typography variant="h3" gutterBottom>
          RSVP
        </Typography>

        <Typography variant="body2" color="textSecondary" sx={{ mb: 6 }}>
          Note: There is an option to choose everyone
        </Typography>

        <form onSubmit={handleSubmit(onSubmit)}>
          <Box
            sx={{
              ...RSVPCreateComponentStyles.formBoxStyles,
              flexDirection: isMobile ? "column" : "row",
              width: isMobile ? "400px" : "700px",
            }}
          >
            <Box
              sx={{
                ...RSVPCreateComponentStyles.firstInputGroupStyles,
                width: isMobile ? "60%" : "60%",
              }}
            >
              <FormAutoComplete
                name="username"
                control={control}
                options={users}
                label="Username"
                required={true}
                error={errors.username?.message}
              />

              <FormAutoComplete
                name="event"
                control={control}
                options={events}
                label="Event"
                required={true}
                error={errors.event?.message}
              />

              <FormAutoComplete
                name="venue"
                control={control}
                options={venues}
                label="Venue (Optional)"
              />
            </Box>

            <Box sx={RSVPCreateComponentStyles.secondInputGroupStyles}>
              <FormAutoComplete
                name="date"
                control={control}
                options={dates}
                label="Date (Optional)"
              />

              <Controller
                name="expirationDate"
                control={control}
                render={({ field }) => (
                  <DatePicker
                    label="Expiration Date"
                    shouldDisableDate={isValidDate}
                    value={undefined}
                    onChange={(newDate) =>
                      field.onChange(newDate?.format("YYYY-MM-DD"))
                    }
                    slotProps={{
                      field: { clearable: true },
                    }}
                    sx={{ width: "100%" }}
                  />
                )}
              />
              <Button
                type="submit"
                variant="contained"
                color="primary"
                sx={RSVPCreateComponentStyles.buttonStyles}
              >
                Send
              </Button>
            </Box>
          </Box>
        </form>
      </Box>
    </LocalizationProvider>
  );
};

export default RSVPCreateComponent;
