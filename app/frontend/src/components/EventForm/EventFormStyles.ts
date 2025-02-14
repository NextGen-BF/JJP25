export const EventFormStyles = {
  formContainer: {
    position: "relative",
    top: "10vh",
    width: "90vw",
    height: "80vh",
    maxWidth: "1200px",
    maxHeight: "680px",
    padding: "20px",
    backgroundColor: "#f5f5f7",
    borderRadius: "10px",
    boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.3)",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",

    "@media (max-width: 768px)": {
      position: "absolute",
      display: "flex",
      flexDirection: "row",
      maxWidth: "80%",
      maxHeight: "80%",
    },
  },

  buttonContainer: {
    width: "96%",
    display: "flex",
    justifyContent: "space-between",
    flex: "0 1 8%",
    maxHeight: "10%",
    height: "auto",
    alignItems: "center",
  },

  leftBox: {
    flex: "0 1 45%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    maxWidth: "45%",
    maxHeight: "100%",
    margin: "20px",
    gap: "16px",
    height: "100%",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      maxHeight: "45%",
      margin: "16px",
      maxWidth: "100%",
      width: "100%",
      gap: "10px",
    },
  },

  rightBox: {
    flex: "0 1 35%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    maxWidth: "35%",
    maxHeight: "100%",
    margin: "20px",
    gap: "24px",
    height: "100%",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      maxHeight: "45%",
      gap: "10px",
      maxWidth: "100%",
      width: "100%",
    },
  },

  eventDatesBox: {},

  selectorsBox: {
    display: "flex",
    flexDirection: "column",
    gap: "16px",
    width: "100%",
    maxWidth: "600px",
  },

  backButton: {
    flex: "0 1 10%",
    display: "flex",
    justifyContent: "left",
  },

  nextButton: {
    flex: "0 1 10%",
    display: "flex",
    justifyContent: "right",
  },
};
