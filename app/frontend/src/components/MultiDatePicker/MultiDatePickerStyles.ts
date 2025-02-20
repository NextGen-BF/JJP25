import "../../styles/globalstyles.scss";

export const MultiDatePickerStyles = {
  boxWrapper: {
    display: "flex",
    flexDirection: "column",
    width: "100%",
    margin: "0 auto",
  },

  datePickerReset: {
    display: "flex",
    flexDirection: "row",
    justifyContent: "space-between",
    width: "100%",
  },

  datePicker: {
    width: "100%",
    marginRight: "4px",
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
    minHeight: "40px",
    maxWidth: "calc(100% + 8px)",
    display: "flex",
    flexDirection: "row",
    spacing: "8px",
    flexWrap: "wrap",
    justifyContent: "flex-start",
  },

  chip: {
    backgroundColor: "var(--blankfactor-ultramarine-color)",
    color: "white",
    fontWeight: "1000",
    marginBottom: "8px",
    width: "calc(33% - 8px)",
    marginRight: "8px",
    marginLeft: "0px",

    "@media (min-width: 768px) and (max-width:1150px)": {
      width: "calc(50% - 8px)",
    },
  },
};
