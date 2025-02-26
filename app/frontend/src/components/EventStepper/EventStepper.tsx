import { useState, FC, useRef, useEffect } from "react";
import {
  Stepper,
  Step,
  StepLabel,
  Button,
  Typography,
  Box,
  TextField,
} from "@mui/material";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import { EventStepperStyles } from "./EventStepperStyles";
import { EventStepperConstants } from "../../constants/EventStepperConstants";
import EventForm from "../EventForm/EventForm";
import VenueForm from "../VenueForm/VenueForm";
import TicketForm from "../TicketForm/TicketForm";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../../redux/store";
import { toast } from "react-toastify";
import { submitEventPayload } from "../../redux/services/eventPayloadService";

const steps = [
  EventStepperConstants.EVENT_CREATION,
  EventStepperConstants.VENUE_CREATION,
  EventStepperConstants.TICKET_CREATION,
];

const EventStepper: FC = () => {
  const [activeStep, setActiveStep] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false); // Flag to check if the form is submitting
  const formRef = useRef<{ submitForm: () => Promise<boolean> } | null>(null);

  const dispatch = useDispatch<AppDispatch>();
  const venueTitle = useSelector(
    (state: RootState) => state.event.event.venueTitle
  ); // from Event state
  const isVenueCreated = useSelector(
    (state: RootState) => state.venue.isVenueCreated
  ); // from Venue state

  const isStepOptional = (step: number) => step === 1;

  const handleNext = async () => {
    if (formRef.current) {
      await formRef.current.submitForm();
    }

    if (activeStep === 1 && !isVenueCreated && !venueTitle) {
      toast.error("Please select or create a new venue to proceed.");
      return;
    }
    if (activeStep === steps.length - 1) {
      await handleFinish();
    } else {
      setActiveStep((prevActiveStep) => prevActiveStep + 1);
    }
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleReset = () => {
    setActiveStep(0);
  };

  const handleFinish = async () => {
    if (isSubmitting) return;
    setIsSubmitting(true);

    try {
      await dispatch(submitEventPayload());
      toast.success("Event submitted successfully!");
      setActiveStep(steps.length);
    } catch (error) {
      toast.error("An error occurred while submitting the event.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderStepForm = () => {
    switch (activeStep) {
      case 0:
        return <EventForm ref={formRef} />;
      case 1:
        return <VenueForm />;
      case 2:
        return <TicketForm />;
      default:
        return null;
    }
  };

  return (
    <Box sx={EventStepperStyles.stepperContainer}>
      <Stepper activeStep={activeStep} sx={EventStepperStyles.stepper}>
        {steps.map((label, index) => {
          const stepProps: { completed?: boolean } = {};
          const labelProps: { optional?: React.ReactNode } = {};

          if (isStepOptional(index)) {
            labelProps.optional = (
              <Typography variant="caption">
                {EventStepperConstants.OPTIONAL_STEP}
              </Typography>
            );
          }

          return (
            <Step key={label} {...stepProps}>
              <StepLabel
                {...labelProps}
                sx={{
                  ...EventStepperStyles.stepLabel(activeStep, index),
                  ...EventStepperStyles.stepIcon(activeStep, index),
                }}
              >
                {label}
              </StepLabel>
            </Step>
          );
        })}
      </Stepper>
      {activeStep === steps.length ? (
        <>
          <Typography sx={{ mt: 2, mb: 1 }}>
            {EventStepperConstants.ALL_STEPS_COMPLETED}
          </Typography>
          <Box sx={EventStepperStyles.ButtonBox}>
            <Box sx={{ flex: "1 1 auto" }} />
            <Button sx={EventStepperStyles.stepButton} onClick={handleReset}>
              {EventStepperConstants.RESET_STEPS}
            </Button>
          </Box>
        </>
      ) : (
        <>
          {/* Box for form and buttons for navigation of the stepper */}
          <Box sx={{ pt: 2 }}>
            <Box sx={EventStepperStyles.ButtonBox}>
              <Button
                disabled={activeStep === 0}
                sx={EventStepperStyles.stepButton}
                onClick={handleBack}
              >
                <ArrowBackIosIcon
                  sx={EventStepperStyles.arrowBackIos(activeStep === 0)}
                />
              </Button>
              <Box sx={{ flex: "1 1 auto" }} />
              {/* {isStepOptional(activeStep) && (
                <Button color="inherit" onClick={handleSkip} sx={{ mr: 1 }}>
                  Skip
                </Button>
              )} */}
              <Button onClick={handleNext} sx={EventStepperStyles.stepButton}>
                {activeStep === steps.length - 1 ? (
                  EventStepperConstants.FINISH_STEP
                ) : (
                  <ArrowForwardIosIcon
                    sx={EventStepperStyles.arrowForwardIos}
                  />
                )}
              </Button>
            </Box>
            {renderStepForm()}
          </Box>
        </>
      )}
    </Box>
  );
};

export default EventStepper;
