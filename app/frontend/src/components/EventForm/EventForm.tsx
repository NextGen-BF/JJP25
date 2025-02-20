import { useForm, Controller } from "react-hook-form";
import { Box, Typography } from "@mui/material";
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
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { forwardRef, useEffect, useImperativeHandle, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  AgeRestriction,
  createEvent,
  EventCategory,
  updateEvent,
} from "../../redux/slices/eventSlice";
import { Event } from "../../redux/slices/eventSlice";
import { RootState } from "../../redux/store";

const EventForm = forwardRef((props, ref: React.ForwardedRef<unknown>) => {
  const dispatch = useDispatch();
  const { event, isNewEvent } = useSelector((state: RootState) => state.event);
  const categories = Object.values(EventCategory);
  const ageRestrictions = Object.values(AgeRestriction);

  // Will be added in the future when we have the venues slice and venues data

  // const venues = useSelector((state: RootState) => state.venue.venues);
  // useEffect(() => { dispatch(fetchVenues()); }, [dispatch]);

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    setValue,
  } = useForm({
    defaultValues: event,
    mode: "onChange",
    reValidateMode: "onSubmit",
  });

  const handleChange = (field: keyof Event, value: any) => {
    dispatch(updateEvent({ [field]: value }));
    setValue(field, value);
  };

  const initialEventRef = useRef<Event>(event);
  const isInitialRender = useRef(true);

  useEffect(() => {
    if (isInitialRender.current) {
      initialEventRef.current = event;
      isInitialRender.current = false;
    }
  }, [event]);

  const onSubmit = (event: Event) => {
    if (isNewEvent) {
      dispatch(createEvent(event));
      toast.success(EventFormConstants.TOAST_MESSAGES.SUCCESS_EVENT_CREATION);
    } else {
      const isEventChanged =
        JSON.stringify(event) !== JSON.stringify(initialEventRef.current);

      if (isEventChanged) {
        dispatch(updateEvent(event));
        toast.info(EventFormConstants.TOAST_MESSAGES.SUCCESS_EVENT_UPDATE);
      }
    }
  };

  // Will be used when all the steps are created
  // const submitEventToBackend = async () => {
  //   try {
  //     console.log("Submitting event to backend:", event);
  //     toast.success("Event submitted successfully!");
  //   } catch (error) {
  //     toast.error("Failed to submit event.");
  //   }
  // };

  useImperativeHandle(ref, () => ({
    submitForm: async () => {
      const isFormValid = await trigger();

      if (isFormValid) {
        handleSubmit(onSubmit)();
        return Promise.resolve(true);
      } else {
        return Promise.reject(false);
      }
    },
  }));

  const venueValidation = {
    validate: (value: string) =>
      EventFormConstants.VALIDATIONS.VENUE.VALID_OPTIONS.includes(value)
        ? true
        : EventFormConstants.VALIDATIONS.VENUE.INVALID_SELECTION,
  };

  return (
    <Box sx={EventFormStyles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className="event-form">
        <Box sx={EventFormStyles.leftBox}>
          <FormInput
            name={EventFormConstants.NAMES.EVENT_TITLE}
            control={control}
            label={EventFormConstants.LABELS.EVENT_TITLE}
            defaultValue={event.title}
            required
            onChange={(e) => handleChange("title", e.target.value)}
            rules={{
              minLength: {
                value:
                  EventFormConstants.VALIDATIONS.EVENT_TITLE.MIN_LENGTH.VALUE,
                message:
                  EventFormConstants.VALIDATIONS.EVENT_TITLE.MIN_LENGTH.MESSAGE,
              },

              pattern: {
                value: EventFormConstants.VALIDATIONS.EVENT_TITLE.PATTERN.VALUE,
                message:
                  EventFormConstants.VALIDATIONS.EVENT_TITLE.PATTERN.MESSAGE,
              },
            }}
            error={errors.title?.message}
          />

          <FormInput
            name={EventFormConstants.NAMES.EVENT_DESCRIPTION}
            control={control}
            label={EventFormConstants.LABELS.EVENT_DESCRIPTION}
            defaultValue={event.description}
            required
            onChange={(e) => handleChange("description", e.target.value)}
            rules={{
              minLength: {
                value:
                  EventFormConstants.VALIDATIONS.EVENT_DESCRIPTION.MIN_LENGTH
                    .VALUE,
                message:
                  EventFormConstants.VALIDATIONS.EVENT_DESCRIPTION.MIN_LENGTH
                    .MESSAGE,
              },
            }}
            multiline
            rows={8}
            error={errors.description?.message}
          />
        </Box>

        <Box sx={EventFormStyles.rightBox}>
          <Box sx={EventFormStyles.eventDatesBox}>
            <Typography variant="h6" sx={{ pb: 2 }}>
              {EventFormConstants.LABELS.EVENT_DATES}
            </Typography>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <Controller
                name="dates"
                control={control}
                defaultValue={event.dates || []}
                rules={{
                  required: EventFormConstants.VALIDATIONS.EVENT_DATES.REQUIRED,
                  validate: (value) =>
                    value.length > 0 ||
                    EventFormConstants.VALIDATIONS.EVENT_DATES.VALIDATE,
                }}
                render={({ field }) => (
                  <MultiDatePicker
                    selectedDates={field.value || []}
                    setSelectedDates={(dates: Dayjs[]) => {
                      field.onChange(dates);
                      handleChange("dates", dates);
                    }}
                  />
                )}
              />
            </LocalizationProvider>

            {typeof errors.dates?.message === "string" && (
              <Typography variant="body2" color="error">
                {errors.dates.message}
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
              error={errors.venueTitle?.message}
              onChange={(value) => handleChange("venueTitle", value)}
              defaultValue={event.venueTitle}
            />

            <FormSelect
              name={EventFormConstants.NAMES.CATEGORY}
              control={control}
              label={EventFormConstants.LABELS.CATEGORY}
              options={categories}
              required
              error={errors.category?.message}
              onChange={(e) => handleChange("category", e.target.value)}
              defaultValue=""
            />

            <FormSelect
              name={EventFormConstants.NAMES.AGE_RESTRICTION}
              control={control}
              label={EventFormConstants.LABELS.AGE_RESTRICTION}
              options={ageRestrictions}
              defaultValue={AgeRestriction.ALL_AGES}
              error={errors.ageRestriction?.message}
              onChange={(e) => handleChange("ageRestriction", e.target.value)}
            />
          </Box>
        </Box>
      </form>
    </Box>
  );
});

export default EventForm;
