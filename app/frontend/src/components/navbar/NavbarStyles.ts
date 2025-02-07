import "../../styles/globalstyles.scss"

export const NavbarStyles = {
  navbarStyles: {
    backgroundColor: "var(--navbar-background-color)", 
    width: "100%",
    top: 0,
    left: 0,
    boxShadow: "none",
  },

  toolbarStyles: {
    display: "flex",
    gap: '50px',
    justifyContent: "space-between",
    alignItems: "center",
    paddingLeft: "16px",
    paddingRight: "16px",
    height: "64px",
  },

  boxStyles: { 
    display: "flex",
    width: "100vw",
    alignItems: "center",
    justifyContent: "space-between"
  },

  iconBoxStyles: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "10px"
  },

  iconStyles: {
    textDecoration: "none", 
    color: "inherit"
  }
};
