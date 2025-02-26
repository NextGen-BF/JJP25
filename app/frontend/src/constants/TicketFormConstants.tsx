export const TicketFormConstants = {
  NAMES: {
    DESCRIPTION: "description",
    EVENT_DATE: "eventDate",
    TICKET_TYPE: "ticketType",
    QUANTITY: "quantity",
    PRICE: "price",
  },
  LABELS: {
    DESCRIPTION: "Description",
    EVENT_DATE: "Event Date",
    TICKET_TYPE: "Ticket Type",
    QUANTITY: "Quantity",
    PRICE: "Price",
    ADD_TICKET: "Add Ticket",
    EDIT_TICKET: "Save Ticket",
    CREATED_TICKETS: "Created Tickets",
  },
  VALIDATION_MESSAGES: {
    DESCRIPTION_REQUIRED: "Ticket Description is required",
    DESCRIPTION_MIN_LENGTH: "Description should be at least 10 symbols",
    DESCRIPTION_MIN_VALUE: 10,
    QUANTITY_MIN: "Must be above 0",
    QUANTITY_MIN_VALUE: 1,
    QUANTITY_MAX: "Must be under 1000",
    QUANTITY_MAX_VALUE: 999,
    QUANTITY_WHOLE_NUMBERS: "Whole numbers only",
    QUANTITY_LEADING_ZERO: "No leading zeroes!",
    PRICE_MIN: "Must be above 0",
    PRICE_MIN_VALUE: 0,
    PRICE_MAX: "Must be under 10000",
    PRICE_MAX_VALUE: 9999.99,
    PRICE_FORMAT: "Incorrect format",
  },

  TOAST_MESSAGES: {
    TICKET_EDIT_SUCCESS: "Ticket updated successfully!",
    TICKET_ADD_SUCCESS: "Ticket added successfully!",
  },
};

export const TicketTableConstants = {
  HEADERS: {
    NUMBER: "#",
    TICKET_TYPE: "Ticket Type",
    EVENT_DATE: "Event Date",
    PRICE: "Price",
    QUANTITY: "Quantity",
    ACTIONS: "Actions",
  },
  PAGINATION: {
    ROWS_PER_PAGE_OPTIONS: [3],
  },
  ARIA_LABELS: {
    TICKET_TABLE: "ticket table",
  },
};
