import "../../styles/globalstyles.scss";

export const MultiDatePickerStyles = {
  boxWrapper: {
    display: "flex",
    flexDirection: "column",
    width: "100%",
    margin: "0 auto",
  },

  calendarIcon: {
    "& .MuiSvgIcon-root": { color: "var(--blankfactor-ultramarine-color)" },
  },
  calendarPicker: {
    "& .MuiPaper-root": { backgroundColor: "#282c34", color: "#ffffff" },
    "& .MuiPickersDay-root": { color: "#ff9800" },
    "& .Mui-selected": { backgroundColor: "#ff9800", color: "#ffffff" },
  },

  stackWrapper: {
    width: "100%",
    maxWidth: "calc(100% + 8px)",
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "flex-start",
  },

  chip: {
    backgroundColor: "var(--blankfactor-ultramarine-color)",
    color: "white",
    fontWeight: "1000",
    marginBottom: "8px",
    width: "calc(50% - 8px)",
    marginRight: "8px",
    marginLeft: "0",
  },
};
