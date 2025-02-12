import { EventStepperConstants } from "../../constants/EventStepperConstants";

export const EventStepperStyles = {
    stepperContainer: {
      width: '100%',
      maxWidth: '1000px',
      margin: '10px auto 0', 
      padding: '16px',
    },

    stepLabel: {
        active: EventStepperConstants.ACTIVE_COLOR, 
        future: EventStepperConstants.INACTIVE_COLOR, 
    },
    stepIcon: {
        active: EventStepperConstants.ACTIVE_COLOR, 
        future: EventStepperConstants.INACTIVE_COLOR, 
    },

    arrowBackIos: (disabled: boolean) => ({
      color: disabled ? '#666' : EventStepperConstants.SECONDARY_COLOR,  // Gray if disabled
      cursor: disabled ? 'not-allowed' : 'pointer',
      transition: 'color 0.3s ease-in-out',
      fontSize: "2rem",
    }),

    arrowForwardIos: {
      color: EventStepperConstants.SECONDARY_COLOR,
      transition: 'color 0.3s ease-in-out',
      fontSize: "2rem",
    },

    stepButton: {
      color: EventStepperConstants.SECONDARY_COLOR,
      display: 'flex',
      alignItems: 'center'
    }
  };
  