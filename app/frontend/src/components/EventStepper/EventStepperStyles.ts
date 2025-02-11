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
  };
  