export const VenueFormStyles = {
  formContainer: {
    position: "relative",
    width: "100%",
    height: "100%",
    maxWidth: "1200px",
    maxHeight: "680px",
    backgroundColor: "#f5f5f7",
    borderRadius: "15px",
    boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.3)",
    display: "flex",
    padding: "0 clamp(20px, 8%, 100px)",

    "@media (max-width: 768px)": {
      display: "flex",
      flexDirection: "row",
      maxHeight: "100%",
      height: "calc(100vh - 144px - 120px)", // Subtract navbar and footer height
      overflowY: "scroll",
      padding: "0 20px",
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
      paddingTop: "0px",
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
    justifyContent: "flex-start",
    maxWidth: "40%",
    maxHeight: "100%",
    gap: "8px",
    height: "100%",

    "@media (max-width: 768px)": {
      flex: "0 1 45%",
      paddingTop: "0px",
      maxHeight: "45%",
      maxWidth: "90%",
      width: "90%",
      gap: "0px",
    },
  },

  fieldsBox: {
    display: "flex",
    flexDirection: "column",
    gap: "8px",
    width: "100%",

    "@media (max-width: 768px)": {
      gap: "0px",
    },
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
