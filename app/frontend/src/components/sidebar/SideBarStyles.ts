export const SideBarStyles = {
  drawerContentBoxStyles: {
    display: "flex",
    flexDirection: "column",
    height: "110vh",
    bgcolor: "#D2D2DA",
    overflow: "hidden",
  },

  drawerContentInnerBoxStyles: { 
    display: "flex", 
    justifyContent: "flex-start", 
    p: 1.5 
  },

  drawerStyles: {
      boxSizing: "border-box",
      transition: "width 0.3s",
      top: 64,
      height: `calc(100vh - ${64}px - ${80}px)`,
  },

  listSubheaderStyles: {
    backgroundColor: "#D2D2DA",
    transition: "0.3s ease"
  },

  listItemTextStyles: {
    whiteSpace: "nowrap",
    color: "#000",
    textDecoration: "none",
    overflow: "hidden",
  },

  listItemButtonStyles: {
    justifyContent: "flex-start",
    pr:0,
    height: 48,
  },


};
