import React from "react";
import { useForm, Controller } from "react-hook-form";
import { Box, Typography, Button } from "@mui/material";
import { EventFormStyles } from "./EventFormStyles";
import "./EventFormStyles.scss";
import FormSelect from "../FormSelect/FormSelect";
import FormInput from "../FormInput/FormInput";
import { EventFormConstants } from "../../constants/EventFormConstants";
import FormAutoComplete from "../FormAutoComplete/FormAutoComplete";

const EventForm: React.FC = () => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const onSubmit = (data: any) => {
    console.log(data);
  };

  return (
    <Box sx={EventFormStyles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className="event-form">
        <Box sx={EventFormStyles.backButton}>
          <Button variant="outlined">Back</Button>
        </Box>

        <Box sx={EventFormStyles.leftBox}>
          <FormInput
            name={EventFormConstants.NAMES.EVENT_TITLE}
            control={control}
            label={EventFormConstants.LABELS.EVENT_TITLE}
            required
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
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6">Event Dates</Typography>
            {/* <MultiDateCalendar /> */}
          </Box>

          <Box sx={EventFormStyles.selectorsBox}>
            <FormAutoComplete
              name="venue"
              control={control}
              label="Venue"
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
      </form>
    </Box>
  );
};

export default EventForm;
