import { EventStepperConstants } from "../../constants/EventStepperConstants";

export const EventStepperStyles = {
  stepper: {
    "& .MuiStepConnector-line": {
      borderTopWidth: "2px",
      transition: "border-color 0.3s ease-in-out",
    },
    "& .MuiStepConnector-root.Mui-active .MuiStepConnector-line": {
      borderColor: EventStepperConstants.ACTIVE_COLOR,
    },
    "& .MuiStepConnector-root.Mui-completed .MuiStepConnector-line": {
      borderColor: EventStepperConstants.ACTIVE_COLOR,
    },
  },

  stepperContainer: {
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    width: "90%",
    maxWidth: "1200px",
    margin: "10px auto 0",
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
    color: disabled
      ? EventStepperConstants.INACTIVE_COLOR
      : EventStepperConstants.SECONDARY_COLOR,
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
  },

  ButtonBox: {
    display: "flex",
    flexDirection: "row",
    mb: 0.5,
  },
};
