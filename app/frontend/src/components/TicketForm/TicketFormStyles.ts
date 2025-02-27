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
      paddingTop: "20px",
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
      paddingTop: "0px",
      paddingBottom: "0px",
      maxHeight: "auto",
      maxWidth: "90%",
      width: "90%",
      gap: "0px",
    },
  },

  tableBox: {
    display: "flex",
    flexDirection: "column",
    paddingBottom: "150px",

    "@media (max-width: 768px)": {
      paddingBottom: "20px",
    },
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

  buttonBox: {
    display: "flex",
    alignItems: "flex-start",
  },

  button: {
    mt: "30px",
    backgroundColor: "transparent",
    border: "1px solid var(--blankfactor-ultramarine-color)",
    broderRadius: "4px",
    color: "var(--blankfactor-ultramarine-color)",
    maxHeight: "56px",
    maxWidth: "80px",
    fontSize: "14px",
    cursor: "pointer",
    transition: "all 0.3s ease",
    ":hover": {
      backgroundColor: "var(--blankfactor-ultramarine-color)",
      color: "white",
    },
    ":focus": {
      outline: "none",
      boxShadow: "0 0 0 2px rgba(0, 0, 0, 0.2)",
    },

    "@media (max-width: 768px)": {
      mt: "24px",
    },
  },
};

export const TicketTableStyles = {
  ticketTable: {
    backgroundColor: "var(--anti-flash-white-color)",
    border: "1px solid var(--blankfactor-ultramarine-color)",
    borderRadius: "4px",
    width: "100%",
  },

  ticketTablePagination: {
    backgroundColor: "var(--anti-flash-white-color)",
    border: "1px solid var(--blankfactor-ultramarine-color)",
  },

  deleteIcon: {
    color: "var(--error-color)",
    fontSize: "24px",

    "@media (max-width: 768px)": {
      fontSize: "16px",
    },
  },

  editIcon: {
    color: "var(--blankfactor-ultramarine-color)",
    fontSize: "24px",

    "@media (max-width: 768px)": {
      fontSize: "16px",
    },
  },

  tableCell: {
    "@media (max-width: 768px)": {
      fontSize: "11px",
      padding: "5px",
    },
  },
};
