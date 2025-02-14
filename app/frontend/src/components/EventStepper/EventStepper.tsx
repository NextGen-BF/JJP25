import { useState, FC, Fragment } from "react";
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

const steps = [
  EventStepperConstants.EVENT_CREATION,
  EventStepperConstants.VENUE_CREATION,
  EventStepperConstants.TICKET_CREATION,
];

const EventStepper: FC = () => {
  const [activeStep, setActiveStep] = useState(0);
  const [skipped, setSkipped] = useState(new Set<number>());

  const isStepOptional = (step: number) => step === 1;

  const isStepSkipped = (step: number) => skipped.has(step);

  const handleNext = () => {
    let newSkipped = skipped;
    if (isStepSkipped(activeStep)) {
      newSkipped = new Set(newSkipped.values());
      newSkipped.delete(activeStep);
    }

    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    setSkipped(newSkipped);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleSkip = () => {
    if (!isStepOptional(activeStep)) {
      throw new Error(EventStepperConstants.Errors.SKIP_ERROR);
    }

    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    setSkipped((prevSkipped) => {
      const newSkipped = new Set(prevSkipped.values());
      newSkipped.add(activeStep);
      return newSkipped;
    });
  };

  const handleReset = () => {
    setActiveStep(0);
  };

  const renderStepForm = () => {
    switch (activeStep) {
      case 0:
        return (
          <Box sx={{ mt: 2 }}>
            <TextField label="Event Name" fullWidth sx={{ mb: 2 }} />
            <TextField label="Event Description" fullWidth sx={{ mb: 2 }} />
          </Box>
        );
      case 1:
        return (
          <Box sx={{ mt: 2 }}>
            <TextField label="Venue Name" fullWidth sx={{ mb: 2 }} />
            <TextField label="Venue Address" fullWidth sx={{ mb: 2 }} />
          </Box>
        );
      case 2:
        return (
          <Box sx={{ mt: 2 }}>
            <TextField label="Ticket Type" fullWidth sx={{ mb: 2 }} />
            <TextField label="Ticket Price" fullWidth sx={{ mb: 2 }} />
          </Box>
        );
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
          if (isStepSkipped(index)) {
            stepProps.completed = false;
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
          <Box sx={EventStepperStyles.defaultBoxStyling}>
            <Box sx={{ flex: "1 1 auto" }} />
            <Button sx={EventStepperStyles.stepButton} onClick={handleReset}>
              {EventStepperConstants.RESET_STEPS}
            </Button>
          </Box>
        </>
      ) : (
        <>
          <Typography sx={{ mt: 2, mb: 1 }}>
            {EventStepperConstants.CURR_STEP} {activeStep + 1}
          </Typography>
          {/* Box for form and buttons for navigation of the stepper */}
          <Box sx={{ mt: 2 }}>
            {renderStepForm()}
            <Box sx={EventStepperStyles.defaultBoxStyling}>
              <Button
                color="inherit"
                disabled={activeStep === 0}
                onClick={handleBack}
              >
                <ArrowBackIosIcon
                  sx={EventStepperStyles.arrowBackIos(activeStep === 0)}
                  fontSize="small"
                />
              </Button>
              <Box sx={{ flex: "1 1 auto" }} />
              {isStepOptional(activeStep) && (
                <Button color="inherit" onClick={handleSkip} sx={{ mr: 1 }}>
                  Skip
                </Button>
              )}
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
          </Box>
        </>
      )}
    </Box>
  );
};

export default EventStepper;
