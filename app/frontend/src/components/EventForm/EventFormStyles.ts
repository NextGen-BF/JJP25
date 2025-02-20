export const EventFormStyles = {
  formContainer: {
    position: "relative",
    width: "90vw",
    height: "80vh",
    maxWidth: "1200px",
    maxHeight: "680px",
    backgroundColor: "#f5f5f7",
    borderRadius: "15px",
    boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.3)",
    display: "flex",
    padding: "0 50px",
    overflow: "hidden",

    "@media (max-width: 768px)": {
      position: "absolute",
      display: "flex",
      flexDirection: "row",
      justifyContent: "flex-start",
      maxWidth: "90%",
      maxHeight: "100%",
      height: "77%",
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
      flex: "0 1 50%",
      maxHeight: "50%",
      paddingTop: "10px",
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
    height: "100%",
    paddingTop: "120px",

    "@media (max-width: 768px)": {
      flex: "0 1 40%",
      maxHeight: "40%",
      paddingTop: "10px",
      maxWidth: "100%",
      width: "100%",
    },
  },

  selectorsBox: {
    display: "flex",
    flexDirection: "column",
    width: "100%",
    maxWidth: "100%",

    "@media (max-width: 768px)": {
      maxWidth: "100%",
    },
  },
};
