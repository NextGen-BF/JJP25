import { EventStepperConstants } from "../../constants/EventStepperConstants";

export const EventStepperStyles = {
  stepper: {
    "& .MuiStepConnector-line": {
      borderTopWidth: "2px",
      transition: "border-color 0.3s ease-in-out", // Smooth transition for color change
    },
    "& .MuiStepConnector-root.Mui-active .MuiStepConnector-line": {
      borderColor: "#2b6aff",
    },
    "& .MuiStepConnector-root.Mui-completed .MuiStepConnector-line": {
      borderColor: "#2b6aff",
    },
  },

  stepperContainer: {
    width: "100%",
    maxWidth: "1000px",
    margin: "10px auto 0",
    padding: "16px",
  },

  stepLabel: (activeStep: number, index: number) => ({
    "& .MuiStepLabel-label": {
      color:
        index <= activeStep
          ? EventStepperConstants.ACTIVE_COLOR
          : EventStepperConstants.INACTIVE_COLOR,
    },
  }),

  stepIcon: (activeStep: number, index: number) => ({
    "& .MuiStepIcon-root": {
      color:
        index <= activeStep
          ? EventStepperConstants.ACTIVE_COLOR
          : EventStepperConstants.INACTIVE_COLOR,
    },
  }),

  arrowBackIos: (disabled: boolean) => ({
    color: disabled ? "#666" : EventStepperConstants.SECONDARY_COLOR, // Gray if disabled
    cursor: disabled ? "not-allowed" : "pointer",
    transition: "color 0.3s ease-in-out",
    fontSize: "2rem",
  }),

  arrowForwardIos: {
    color: EventStepperConstants.SECONDARY_COLOR,
    transition: "color 0.3s ease-in-out",
    fontSize: "2rem",
  },

  stepButton: {
    color: EventStepperConstants.SECONDARY_COLOR,
    display: "flex",
    alignItems: "center",
  },

  defaultBoxStyling: {
    display: "flex",
    flexDirection: "row",
    pt: 2,
  },
};
