import { useForm, Controller } from "react-hook-form";
import { Box, Button, Typography } from "@mui/material";
import { VenueFormStyles } from "./VenueFormStyles";
import { VenueFormConstants } from "../../constants/VenueFormConstants";
import "./VenueFormStyles.scss";
import FormSelect from "../FormSelect/FormSelect";
import FormInput from "../FormInput/FormInput";
import FormAutoComplete from "../FormAutoComplete/FormAutoComplete";
import { toast } from "react-toastify";
import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { FC } from "react";
import {
  Venue,
  VenueType,
  createVenue,
  updateVenue,
} from "../../redux/slices/venueSlice";
import ButtonComponent from "../button/ButtonComponent";
import { z } from "zod";
import { FormInputStyles } from "../FormInput/FormInputStyles";

const venueValidationSchema = {
  phone: z
    .string()
    .regex(
      VenueFormConstants.VALIDATION.PHONE_REGEX,
      VenueFormConstants.VALIDATION.PHONE
    ),

  website: z.string().url(VenueFormConstants.VALIDATION.WEBSITE),
};

const VenueForm: FC = () => {
  const dispatch = useDispatch();
  const { venue, isNewVenue } = useSelector((state: RootState) => state.venue);
  const venueTypes = Object.values(VenueType);
  const [isSubmitted, setIsSubmitted] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    setValue,
  } = useForm({
    defaultValues: venue,
    mode: "onChange",
    reValidateMode: "onSubmit",
    shouldUnregister: true,
  });

  const handleChange = async (field: keyof Venue, value: any) => {
    dispatch(updateVenue({ [field]: value }));
    setValue(field, value);
    await trigger(field);
  };

  const initialVenueRef = useRef<Venue>(venue);
  const isInitialRender = useRef(true);

  useEffect(() => {
    if (isInitialRender.current) {
      initialVenueRef.current = venue;
      isInitialRender.current = false;
    }
  }, [venue]);

  const onSubmit = async (venue: Venue) => {
    const isFormValid = await trigger();

    if (!isFormValid) {
      return;
    }

    if (!isSubmitted) {
      dispatch(createVenue(venue));
      toast.success(VenueFormConstants.TOAST_MESSAGES.SUCCESS_VENUE_CREATION);
      setIsSubmitted(true);
    } else {
      const isVenueChanged =
        JSON.stringify(venue) !== JSON.stringify(initialVenueRef.current);

      if (isVenueChanged) {
        dispatch(updateVenue(venue));
        toast.info(VenueFormConstants.TOAST_MESSAGES.SUCCESS_VENUE_UPDATE);
      }
    }
  };

  return (
    <Box sx={VenueFormStyles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className="venue-form">
        <Box sx={VenueFormStyles.leftBox}>
          <FormInput
            name={VenueFormConstants.NAMES.VENUE_NAME}
            control={control}
            label={VenueFormConstants.LABELS.VENUE_NAME}
            defaultValue={venue.name}
            required
            onChange={(e) => handleChange("name", e.target.value)}
            error={errors.name?.message as string}
          />

          <FormInput
            name={VenueFormConstants.NAMES.PHONE}
            control={control}
            label={VenueFormConstants.LABELS.PHONE}
            defaultValue={venue.phone}
            error={errors.phone?.message as string}
            onChange={(e) => handleChange("phone", e.target.value)}
            rules={{
              required: false,
              validate: (value: string) => {
                if (!value) return true;
                return (
                  venueValidationSchema.phone.safeParse(value).success ||
                  VenueFormConstants.VALIDATION.PHONE_FORMAT
                );
              },
            }}
          />
          <FormInput
            name={VenueFormConstants.NAMES.WEBSITE}
            control={control}
            label={VenueFormConstants.LABELS.WEBSITE}
            defaultValue={venue.website}
            error={errors.website?.message as string}
            onChange={(e) => handleChange("website", e.target.value)}
            rules={{
              required: false,
              validate: (value: string) => {
                if (!value) return true;
                return (
                  venueValidationSchema.website.safeParse(value).success ||
                  VenueFormConstants.VALIDATION.WEBSITE
                );
              },
            }}
          />

          <Box sx={{ pt: 3 }}>
            <FormSelect
              name={VenueFormConstants.NAMES.TYPE}
              control={control}
              label={VenueFormConstants.LABELS.TYPE}
              options={venueTypes}
              defaultValue={venue.type}
              required
              error={errors.type?.message as string}
              onChange={(value) => handleChange("type", value)}
            />
          </Box>
        </Box>

        <Box sx={VenueFormStyles.rightBox}>
          <Box sx={VenueFormStyles.fieldsBox}>
            <Typography sx={FormInputStyles.typography}>
              {VenueFormConstants.LABELS.COUNTRY}
            </Typography>
            <FormAutoComplete
              name={VenueFormConstants.NAMES.COUNTRY}
              control={control}
              label={VenueFormConstants.LABELS.COUNTRY}
              defaultValue={venue.country}
              options={VenueFormConstants.COUNTRIES}
              required
              error={errors.country?.message as string}
              onChange={(value) => handleChange("country", value)}
            />
            <FormInput
              name={VenueFormConstants.NAMES.CITY}
              control={control}
              label={VenueFormConstants.LABELS.CITY}
              defaultValue={venue.city}
              required
              error={errors.city?.message as string}
              onChange={(e) => handleChange("city", e.target.value)}
            />
            <FormInput
              name={VenueFormConstants.NAMES.ADDRESS}
              control={control}
              label={VenueFormConstants.LABELS.ADDRESS}
              defaultValue={venue.address}
              multiline
              rows={3}
              required
              error={errors.address?.message as string}
              onChange={(e) => handleChange("address", e.target.value)}
            />
          </Box>

          <Box sx={VenueFormStyles.buttonBox}>
            <ButtonComponent
              label={VenueFormConstants.BUTTON_NAME}
              color="primary"
              hoverColor="secondary"
              size="medium"
              onClickHandler={() => handleSubmit(onSubmit)()}
            />
          </Box>
        </Box>
      </form>
    </Box>
  );
};

export default VenueForm;
