export const VenueFormConstants = {
  BUTTON_NAME: "Attach to Event",
  NAMES: {
    VENUE_NAME: "name",
    COUNTRY: "country",
    CITY: "city",
    ADDRESS: "address",
    TYPE: "type",
    PHONE: "phone",
    WEBSITE: "website",
  },

  LABELS: {
    VENUE_NAME: "Venue Name",
    COUNTRY: "Country",
    CITY: "City",
    ADDRESS: "Address",
    TYPE: "Venue Type",
    PHONE: "Phone Number",
    WEBSITE: "Website",
  },

  TOAST_MESSAGES: {
    SUCCESS_VENUE_CREATION: "Venue successfully created!",
    SUCCESS_VENUE_UPDATE: "Venue successfully updated!",
  },

  COUNTRIES: [
    "Bulgaria",
    "United States",
    "Canada",
    "United Kingdom",
    "Germany",
    "France",
    "Italy",
    "Spain",
    "Australia",
    "Brazil",
    "Japan",
    "India",
    "China",
    "Mexico",
  ],

  VALIDATIONS: {
    REQUIRED: "This field is required.",
    PHONE: {
      PATTERN: {
        VALUE: /^[0-9]{10,15}$/,
        MESSAGE: "Please enter a valid phone number.",
      },
    },
    WEBSITE: {
      PATTERN: {
        VALUE: /^(https?:\/\/)?([\w\d-]+\.)+[\w\d]{2,}(\/.*)?$/,
        MESSAGE: "Please enter a valid website URL.",
      },
    },
  },
};
