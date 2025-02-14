import { createTheme } from "@mui/material/styles";
import "../../styles/globalstyles.scss"

export const MultiDatePickerStyles = {
    boxWrapper: {
        display: 'flex',
        flexDirection: 'column',
        gap: '16px',
        width: '100%',
        maxWidth: '400px',
        margin: '0 auto',
        padding: '16px',
        backgroundColor: '#f5f5f5',
        borderRadius: '5px', 
        boxShadow: '0 4px 5px rgba(0, 0, 0, 0.1)',
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
        width: "calc(33.33% - 8px)",
        marginRight: "8px",
        marginLeft: "0",
    },
  };
  