export const TicketFormStyles = {
  leftBox: {
    flex: "0 1 50%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "flex-start",
    maxWidth: "50%",
    maxHeight: "100%",
    paddingTop: "70px",
    paddingBottom: "50px",
    gap: "8px",
    height: "100%",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      paddingBottom: "0px",
      maxHeight: "auto",
      maxWidth: "90%",
      width: "90%",
      gap: "0px",
    },
  },

  rightBox: {
    flex: "0 1 40%",
    display: "flex",
    flexDirection: "column",
    paddingTop: "106px",
    paddingBottom: "50px",
    justifyContent: "flex-start",
    maxWidth: "40%",
    maxHeight: "100%",
    gap: "8px",
    height: "100%",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      paddingBottom: "0px",
      maxHeight: "auto",
      maxWidth: "90%",
      width: "90%",
      gap: "0px",
    },
  },

  tableBox: {
    display: "flex",
    flexDirection: "row",
  },

  formInfoBox: {
    display: "flex",
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "flex-start",
    gap: "60px",
    width: "100%",

    "@media (max-width: 768px)": {
      flexDirection: "column", // Stack vertically on smaller screens
      gap: "16px",
    },
  },

  formTableBox: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    width: "100%",
  },

  quantityPriceButtonBox: {
    display: "flex",
    flexDirection: "row",
    gap: "16px",
  },
};

export const TicketTableStyles = {
  ticketTable: {
    backgroundColor: "var(--separator-line-color)",
    borderRadius: "4px",
    width: "100%",
  },

  ticketTablePagination: {
    backgroundColor: "var(--separator-line-color)",
  },
};
