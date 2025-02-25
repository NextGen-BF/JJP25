export const FormStyles = {
  formContainer: {
    position: "relative",
    width: "100%",
    height: "100%",
    maxWidth: "1200px",
    maxHeight: "680px",
    backgroundColor: "var(--anti-flash-white-color)",
    borderRadius: "15px",
    boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.3)",
    display: "flex",
    padding: "0 clamp(20px, 8%, 100px)",

    "@media (max-width: 768px)": {
      display: "flex",
      flexDirection: "row",
      maxHeight: "100%",
      height: "calc(100vh - 144px - 120px)",
      overflowY: "scroll",
      padding: "0 20px",
    },
  },
};
