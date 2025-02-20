export const EventFormStyles = {
  formContainer: {
    position: "relative",
    width: "100vw",
    height: "80vh",
    maxWidth: "1000px",
    maxHeight: "680px",
    backgroundColor: "#f5f5f7",
    borderRadius: "15px",
    boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.3)",
    display: "flex",
    padding: "0 8%",

    "@media (max-width: 768px)": {
      position: "absolute",
      display: "flex",
      flexDirection: "row",
      justifyContent: "flex-start",
      maxWidth: "90%",
      maxHeight: "100%",
      height: "85%",
      overflowY: "scroll",
      padding: "0 5%",
    },
  },

  leftBox: {
    flex: "0 1 50%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "flex-start",
    maxWidth: "50%",
    maxHeight: "100%",
    gap: "16px",
    height: "100%",
    paddingTop: "120px",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      maxHeight: "45%",
      paddingTop: "20px",
      maxWidth: "100%",
      width: "100%",
      gap: "10px",
    },
  },

  rightBox: {
    flex: "0 1 40%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "flex-start",
    maxWidth: "40%",
    maxHeight: "100%",
    gap: "24px",
    height: "100%",
    paddingTop: "120px",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      maxHeight: "45%",
      paddingTop: "20px",
      gap: "12px",
      maxWidth: "100%",
      width: "100%",
    },
  },

  eventDatesBox: {},

  selectorsBox: {
    display: "flex",
    flexDirection: "column",
    gap: "24px",
    width: "100%",
    maxWidth: "600px",

    "@media (max-width: 768px)": {
      gap: "12px",
      maxWidth: "100%",
    },
  },
};
