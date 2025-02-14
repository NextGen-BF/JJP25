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

  const isMobile = useMediaQuery("(max-width: 768px)");

  const onSubmit = (data: any) => {
    console.log(data);
    toast.success(EventFormConstants.TOAST_MESSAGES.SUCCESS_EVENT_CREATION);
  };

  const venueValidation = {
    validate: (value: string) =>
      EventFormConstants.VALIDATIONS.VENUE.VALID_OPTIONS.includes(value)
        ? true
        : EventFormConstants.VALIDATIONS.VENUE.INVALID_SELECTION,
  };

  return (
    <Box sx={EventFormStyles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className="event-form">
        {isMobile ? (
          <>
            {/* Will use the buttons from the Stepper in the actual Page */}
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
                    value:
                      EventFormConstants.VALIDATIONS.EVENT_TITLE.MIN_LENGTH
                        .VALUE,
                    message:
                      EventFormConstants.VALIDATIONS.EVENT_TITLE.MIN_LENGTH
                        .MESSAGE,
                  },

                  pattern: {
                    value:
                      EventFormConstants.VALIDATIONS.EVENT_TITLE.PATTERN.VALUE,
                    message:
                      EventFormConstants.VALIDATIONS.EVENT_TITLE.PATTERN
                        .MESSAGE,
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
                    value:
                      EventFormConstants.VALIDATIONS.EVENT_DESCRIPTION
                        .MIN_LENGTH.VALUE,
                    message:
                      EventFormConstants.VALIDATIONS.EVENT_DESCRIPTION
                        .MIN_LENGTH.MESSAGE,
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
                <Typography variant="h6">
                  {EventFormConstants.LABELS.EVENT_DATES}
                </Typography>
                <LocalizationProvider dateAdapter={AdapterDayjs}>
                  <Controller
                    name={EventFormConstants.NAMES.EVENT_DATES}
                    control={control}
                    rules={{
                      required:
                        EventFormConstants.VALIDATIONS.EVENT_DATES.REQUIRED,
                      validate: (value) =>
                        value.length > 0 ||
                        EventFormConstants.VALIDATIONS.EVENT_DATES.VALIDATE,
                    }}
                    render={({ field }) => (
                      <MultiDatePicker
                        selectedDates={field.value || []}
                        setSelectedDates={(dates: Dayjs[]) =>
                          field.onChange(dates)
                        }
                      />
                    )}
                  />
                </LocalizationProvider>

                {typeof errors.eventDates?.message === "string" && (
                  <Typography variant="body2" color="error">
                    {errors.eventDates.message}
                  </Typography>
                )}
              </Box>

              {/* All Enums will have actual values when we start using the Events slice */}

              <Box sx={EventFormStyles.selectorsBox}>
                <FormAutoComplete
                  name={EventFormConstants.NAMES.VENUE}
                  control={control}
                  rules={venueValidation}
                  label={EventFormConstants.LABELS.VENUE}
                  options={EventFormConstants.VALIDATIONS.VENUE.VALID_OPTIONS}
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
                  options={EventFormConstants.SELECT_OPTIONS.CATEGORIES}
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
                  options={EventFormConstants.SELECT_OPTIONS.AGE_RESTRICTIONS}
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
                    value:
                      EventFormConstants.VALIDATIONS.EVENT_TITLE.MIN_LENGTH
                        .MESSAGE,
                    message:
                      EventFormConstants.VALIDATIONS.EVENT_TITLE.MIN_LENGTH
                        .MESSAGE,
                  },

                  pattern: {
                    value:
                      EventFormConstants.VALIDATIONS.EVENT_TITLE.PATTERN.VALUE,
                    message:
                      EventFormConstants.VALIDATIONS.EVENT_TITLE.PATTERN
                        .MESSAGE,
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
                    value:
                      EventFormConstants.VALIDATIONS.EVENT_DESCRIPTION
                        .MIN_LENGTH.VALUE,
                    message:
                      EventFormConstants.VALIDATIONS.EVENT_DESCRIPTION
                        .MIN_LENGTH.MESSAGE,
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
                  <Controller
                    name={EventFormConstants.NAMES.EVENT_DATES}
                    control={control}
                    rules={{
                      required:
                        EventFormConstants.VALIDATIONS.EVENT_DATES.REQUIRED,
                      validate: (value) =>
                        value.length > 0 ||
                        EventFormConstants.VALIDATIONS.EVENT_DATES.VALIDATE,
                    }}
                    render={({ field }) => (
                      <MultiDatePicker
                        selectedDates={field.value || []}
                        setSelectedDates={(dates: Dayjs[]) =>
                          field.onChange(dates)
                        }
                      />
                    )}
                  />
                </LocalizationProvider>

                {typeof errors.eventDates?.message === "string" && (
                  <Typography variant="body2" color="error">
                    {errors.eventDates.message}
                  </Typography>
                )}
              </Box>

              <Box sx={EventFormStyles.selectorsBox}>
                <FormAutoComplete
                  name={EventFormConstants.NAMES.VENUE}
                  control={control}
                  rules={venueValidation}
                  label={EventFormConstants.LABELS.VENUE}
                  options={EventFormConstants.VALIDATIONS.VENUE.VALID_OPTIONS}
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
                  options={EventFormConstants.SELECT_OPTIONS.CATEGORIES}
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
                  options={EventFormConstants.SELECT_OPTIONS.AGE_RESTRICTIONS}
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
