import "../../styles/globalstyles.scss"

export const FooterStyles = {
  container: {
    backgroundColor: "var(--navbar-background-color)", 
    width: "100%",
    height: "80px",
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    padding: "0 50px",
    bottom: 0,
    left: 0,
  },

  logoContainer: {
    display: "flex",
    alignItems: "center",
  },

  logo: {
    height: "30px",
  },

  logoLink: {
    textDecoration: 'none',
    display: 'inline-block', 
  },

  nav: {
    display: "flex",
    gap: "20px",
  },

  link: {
    color: "white",
    textDecoration: "none",
    fontSize: "20px",
    fontWeight: "500",
    transition: "color 0.3s ease",
  },

  linkHover: {
    color: "var(--secondary-color)",
  },
};
