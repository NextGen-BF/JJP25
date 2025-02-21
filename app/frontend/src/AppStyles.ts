export const AppStyles = {
  outerBoxStyles: {
    display: "flex",
    flexDirection: "column",
    minHeight: "100vh",
    overflowY: "hidden",
  },
  routesBoxStyles: {
    flexGrow: 1,
    display: "flex",
    flexDirection: "column",
    paddingTop: "60px",
    transition: "0.3s all",
    height: "calc(100vh - 144px)", // 100vh - (Navbar: 60px + Footer: 80px)
  },
};
