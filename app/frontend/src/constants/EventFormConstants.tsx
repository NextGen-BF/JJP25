export const EventFormConstants = {
  NAMES: {
    EVENT_TITLE: "title",
    EVENT_DESCRIPTION: "description",
    EVENT_DATES: "dates",
    VENUE: "venueTitle",
    CATEGORY: "category",
    AGE_RESTRICTION: "ageRestriction",
  },

  LABELS: {
    EVENT_TITLE: "Event Title",
    EVENT_DESCRIPTION: "Description",
    EVENT_DATES: "Event Dates",
    VENUE: "Venue",
    CATEGORY: "Category",
    AGE_RESTRICTION: "Age Restriction",
  },

  VALIDATIONS: {
    EVENT_TITLE: {
      MIN_LENGTH: {
        VALUE: 5,
        MESSAGE: "Event title must be at least 5 characters",
      },
      PATTERN: {
        VALUE: /^[A-Z]/,
        MESSAGE: "Event title must start with a capital letter",
      },
    },
    EVENT_DESCRIPTION: {
      MIN_LENGTH: {
        VALUE: 24,
        MESSAGE: "Event description must be at least 24 characters",
      },
    },
    EVENT_DATES: {
      REQUIRED: "Event dates are required",
      VALIDATE: "Please select at least one date",
    },
    VENUE: {
      VALID_OPTIONS: [
        "Stadium A",
        "Stadium B",
        "Stadium D",
        "Stadium G",
        "Conference Hall",
        "Theater XYZ",
      ],
      INVALID_SELECTION: "Choose a valid venue",
    },
  },

  SELECT_OPTIONS: {
    CATEGORIES: ["Music", "Conference", "Sports"],
    AGE_RESTRICTIONS: ["All Ages", "18+", "21+"],
  },

  TOAST_MESSAGES: {
    SUCCESS_EVENT_CREATION: "Event Created Successfully!",
    SUCCESS_EVENT_UPDATE: "Event Updated Successfully!",
  },
};
