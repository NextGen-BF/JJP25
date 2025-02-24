export const EventFormStyles = {
  leftBox: {
    flex: "0 1 50%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "flex-start",
    maxWidth: "50%",
    maxHeight: "100%",
    paddingTop: "100px",
    paddingBottom: "100px",
    height: "100%",
    gap: "16px",

    "@media (max-width: 768px)": {
      flex: "0 1 50%",
      paddingTop: "0px",
      paddingBottom: "0px",
      maxHeight: "auto%",
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
    height: "100%",

    "@media (max-width: 768px)": {
      flex: "0 1 40%",
      maxHeight: "40%",
      paddingTop: "0px",
      paddingBottom: "0px",
      maxWidth: "90%",
      width: "90%",
    },
  },

  selectorsBox: {
    display: "flex",
    flexDirection: "column",
    width: "100%",
    maxWidth: "100%",
    gap: "8px",

    "@media (max-width: 768px)": {
      maxWidth: "100%",
    },
  },
};
