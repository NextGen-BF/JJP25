import React, { useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { Box, Typography, Button, useMediaQuery } from "@mui/material";
import { EventFormStyles } from "./EventFormStyles";
import "./EventFormStyles.scss";
import FormSelect from "../FormSelect/FormSelect";
import FormInput from "../FormInput/FormInput";
import { EventFormConstants } from "../../constants/EventFormConstants";
import FormAutoComplete from "../FormAutoComplete/FormAutoComplete";
import { Dayjs } from "dayjs";
import MultiDatePicker from "../MultiDatePicker/MultiDatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
// This will be moved in the Stepper/Page in final functionality
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
const EventForm: React.FC = () => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    mode: "onChange",
  });

  const [selectedDates, setSelectedDates] = useState<Dayjs[]>([]);

  const isMobile = useMediaQuery("(max-width: 768px)");

  const onSubmit = (data: any) => {
    console.log(data);
    toast.success("Event Created Successfully!"); // Trigger success toast
  };

  const venueValidation = {
    validate: (value: string) =>
      [
        "Stadium A",
        "Stadium B",
        "Stadium D",
        "Stadium G",
        "Conference Hall",
        "Theater XYZ",
      ].includes(value)
        ? true
        : "Choose a valid venue",
  };

  return (
    <Box sx={EventFormStyles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className="event-form">
        {isMobile ? (
          <>
            <Box sx={EventFormStyles.buttonContainer}>
              <Box sx={EventFormStyles.backButton}>
                <Button variant="outlined">Back</Button>
              </Box>

              <Box sx={EventFormStyles.nextButton}>
                <Button type="submit" variant="contained">
                  Next
                </Button>
              </Box>
            </Box>

            <Box sx={EventFormStyles.leftBox}>
              <FormInput
                name={EventFormConstants.NAMES.EVENT_TITLE}
                control={control}
                label={EventFormConstants.LABELS.EVENT_TITLE}
                required
                rules={{
                  minLength: {
                    value: 5,
                    message: "Event title must be at least 5 characters",
                  },

                  pattern: {
                    value: /^[A-Z]/,
                    message: "Event title must start with a capital letter",
                  },
                }}
                error={
                  typeof errors.eventTitle?.message === "string"
                    ? errors.eventTitle.message
                    : undefined
                }
                sx={EventFormStyles.backButton}
              />

              <FormInput
                name={EventFormConstants.NAMES.EVENT_DESCRIPTION}
                control={control}
                label={EventFormConstants.LABELS.EVENT_DESCRIPTION}
                required
                rules={{
                  minLength: {
                    value: 24,
                    message: "Event description must be at least 24 characters",
                  },
                }}
                multiline
                rows={8}
                error={
                  typeof errors.eventDescription?.message === "string"
                    ? errors.eventDescription.message
                    : undefined
                }
              />
            </Box>

            <Box sx={EventFormStyles.rightBox}>
              <Box sx={EventFormStyles.eventDatesBox}>
                <Typography variant="h6">Event Dates</Typography>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <MultiDatePicker
                    selectedDates={selectedDates}
                    setSelectedDates={setSelectedDates}
                  />
                </LocalizationProvider>
              </Box>

              <Box sx={EventFormStyles.selectorsBox}>
                <FormAutoComplete
                  name={EventFormConstants.NAMES.VENUE}
                  control={control}
                  rules={venueValidation}
                  label={EventFormConstants.LABELS.VENUE}
                  options={[
                    "Stadium A",
                    "Stadium B",
                    "Stadium D",
                    "Stadium G",
                    "Conference Hall",
                    "Theater XYZ",
                  ]} // Example venues
                  required
                  error={
                    typeof errors.venue?.message === "string"
                      ? errors.venue.message
                      : undefined
                  }
                />

                <FormSelect
                  name={EventFormConstants.NAMES.CATEGORY}
                  control={control}
                  label={EventFormConstants.LABELS.CATEGORY}
                  options={["Music", "Conference", "Sports"]}
                  required
                  error={
                    typeof errors.category?.message === "string"
                      ? errors.category.message
                      : undefined
                  }
                />

                <FormSelect
                  name={EventFormConstants.NAMES.AGE_RESTRICTION}
                  control={control}
                  label={EventFormConstants.LABELS.AGE_RESTRICTION}
                  options={["All Ages", "18+", "21+"]}
                  error={
                    typeof errors.ageRestriction?.message === "string"
                      ? errors.ageRestriction.message
                      : undefined
                  }
                />
              </Box>
            </Box>
          </>
        ) : (
          <>
            <Box sx={EventFormStyles.backButton}>
              <Button variant="outlined">Back</Button>
            </Box>

            <Box sx={EventFormStyles.leftBox}>
              <FormInput
                name={EventFormConstants.NAMES.EVENT_TITLE}
                control={control}
                label={EventFormConstants.LABELS.EVENT_TITLE}
                required
                rules={{
                  minLength: {
                    value: 5,
                    message: "Event title must be at least 5 characters",
                  },

                  pattern: {
                    value: /^[A-Z]/,
                    message: "Event title must start with a capital letter",
                  },
                }}
                error={
                  typeof errors.eventTitle?.message === "string"
                    ? errors.eventTitle.message
                    : undefined
                }
                sx={EventFormStyles.backButton}
              />

              <FormInput
                name={EventFormConstants.NAMES.EVENT_DESCRIPTION}
                control={control}
                label={EventFormConstants.LABELS.EVENT_DESCRIPTION}
                required
                rules={{
                  minLength: {
                    value: 24,
                    message: "Event description must be at least 24 characters",
                  },
                }}
                multiline
                rows={8}
                error={
                  typeof errors.eventDescription?.message === "string"
                    ? errors.eventDescription.message
                    : undefined
                }
              />
            </Box>

            <Box sx={EventFormStyles.rightBox}>
              <Box sx={EventFormStyles.eventDatesBox}>
                <Typography variant="h6">Event Dates</Typography>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <MultiDatePicker
                    selectedDates={selectedDates}
                    setSelectedDates={setSelectedDates}
                  />
                </LocalizationProvider>
              </Box>

              <Box sx={EventFormStyles.selectorsBox}>
                <FormAutoComplete
                  name={EventFormConstants.NAMES.VENUE}
                  control={control}
                  rules={venueValidation}
                  label={EventFormConstants.LABELS.VENUE}
                  options={[
                    "Stadium A",
                    "Stadium B",
                    "Stadium D",
                    "Stadium G",
                    "Conference Hall",
                    "Theater XYZ",
                  ]} // Example venues
                  required
                  error={
                    typeof errors.venue?.message === "string"
                      ? errors.venue.message
                      : undefined
                  }
                />

                <FormSelect
                  name={EventFormConstants.NAMES.CATEGORY}
                  control={control}
                  label={EventFormConstants.LABELS.CATEGORY}
                  options={["Music", "Conference", "Sports"]}
                  required
                  error={
                    typeof errors.category?.message === "string"
                      ? errors.category.message
                      : undefined
                  }
                />

                <FormSelect
                  name={EventFormConstants.NAMES.AGE_RESTRICTION}
                  control={control}
                  label={EventFormConstants.LABELS.AGE_RESTRICTION}
                  options={["All Ages", "18+", "21+"]}
                  error={
                    typeof errors.ageRestriction?.message === "string"
                      ? errors.ageRestriction.message
                      : undefined
                  }
                />
              </Box>
            </Box>

            <Box sx={EventFormStyles.nextButton}>
              <Button type="submit" variant="contained">
                Next
              </Button>
            </Box>
          </>
        )}
      </form>
    </Box>
  );
};

export default EventForm;
