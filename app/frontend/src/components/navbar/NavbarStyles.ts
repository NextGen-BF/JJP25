import "../../styles/globalstyles.scss"

export const navbarHeight = 64;
 
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
    paddingLeft: "16px",
    paddingRight: "16px",
    height: navbarHeight,
  },

  boxStyles: { 
    display: "flex",
    width: "100vw",
    alignItems: "center",
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
