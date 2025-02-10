import { AppBar, Toolbar, Button, Box } from "@mui/material";
import { Link } from "react-router-dom";
import Logo from "../assets/whiteLogoBlankfactor.png";
import { NavbarStyles } from "./NavbarStyles";
import "./Navbar.scss"

const Navbar = () => {
  return (
    <AppBar position="fixed" sx={NavbarStyles.navbarStyles}>
      <Toolbar disableGutters sx={NavbarStyles.toolbarStyles}>
        <Box sx={NavbarStyles.boxStyles}>
          <Link
            to="/"
            className="link-styles"
          >
            <img
              src={Logo}
              alt="Event Manager Logo"
              className="logo-styles"
            />
          </Link>
        </Box>

        <Box sx={NavbarStyles.boxStyles}>
          <Button color="inherit" component={Link} to="/events">
            Events
          </Button>
          <Button color="inherit" component={Link} to="/tickets">
            Tickets
          </Button>
          <Button color="inherit" component={Link} to="/notifications">
            Notifications
          </Button>
          <Button color="inherit" component={Link} to="/dashboard">
            Dashboard
          </Button>
        </Box>

        <Box sx={NavbarStyles.boxStyles}>
          <Button color="inherit" component={Link} to="/login">
            Login
          </Button>
          <Button color="inherit" component={Link} to="/register">
            Register
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
