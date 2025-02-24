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

  VALIDATION: {
    PHONE_REGEX: /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/,
    PHONE:
      "Phone number must be valid, including country code (e.g., +359123456789)",
    WEBSITE:
      "Invalid website URL. Use http:// or https:// followed by a valid domain name.",
    REQUIRED: "This field is required",
    INVALID_URL: "Please enter a valid URL",
    PHONE_FORMAT:
      "Phone number must be in a valid format, such as +359123456789",
  },
};
