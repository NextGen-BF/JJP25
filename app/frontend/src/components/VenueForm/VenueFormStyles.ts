export const VenueFormStyles = {
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

    "@media (max-width: 768px)": {
      position: "absolute",
      display: "flex",
      flexDirection: "row",
      maxWidth: "90%",
      maxHeight: "100%",
      height: "85%",
      overflowY: "scroll",
    },
  },

  leftBox: {
    flex: "0 1 50%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "flex-start",
    maxWidth: "50%",
    maxHeight: "100%",
    paddingTop: "100px",
    gap: "8px",
    height: "100%",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      maxHeight: "40%",
      paddingTop: "0px",
      maxWidth: "90%",
      width: "90%",
      gap: "10px",
    },
  },

  rightBox: {
    flex: "0 1 40%",
    display: "flex",
    flexDirection: "column",
    paddingTop: "100px",
    justifyContent: "flex-start",
    maxWidth: "40%",
    maxHeight: "100%",
    gap: "8px",
    height: "100%",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      maxHeight: "50%",
      maxWidth: "90%",
      width: "90%",
    },
  },

  fieldsBox: {
    display: "flex",
    flexDirection: "column",
    gap: "8px",
    width: "100%",
  },

  buttonBox: {
    display: "flex",
    flexDirection: "column",
    alignItems: "flex-end",

    "@media (max-width: 768px)": {
      alignItems: "center",
      paddingBottom: "24px",
    },
  },
};
