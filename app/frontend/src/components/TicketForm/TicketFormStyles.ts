export const TicketFormStyles = {
  leftBox: {
    flex: "0 1 50%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "flex-start",
    maxWidth: "50%",
    maxHeight: "100%",
    paddingTop: "100px",
    paddingBottom: "100px",
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

  rightBox: {
    flex: "0 1 40%",
    display: "flex",
    flexDirection: "column",
    paddingTop: "100px",
    paddingBottom: "100px",
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

  tableBox: {},

  quantityPriceButtonBox: {
    display: "flex",
    flexDirection: "row",
    gap: "16px",
  },
};
