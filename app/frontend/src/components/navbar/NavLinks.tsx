import { Box, Button } from "@mui/material";
import { FC } from "react";
import { Link } from "react-router-dom";
import { NavbarStyles } from "./NavbarStyles";
import NotificationsIcon from "@mui/icons-material/Notifications";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import PersonIcon from "@mui/icons-material/Person";

interface NavLinksProps {
  flexDirection: "row" | "row-reverse" | "column-reverse" | "column";
}

const NavLinks: FC<NavLinksProps> = ({ flexDirection }) => {
  return (
    <Box display={"flex"} flexDirection={flexDirection}>
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
      <Box sx={NavbarStyles.iconBoxStyles}>
        <Link to="/" style={NavbarStyles.iconStyles}>
          <NotificationsIcon />
        </Link>
        <Link to="/" style={NavbarStyles.iconStyles}>
          <ShoppingCartIcon />
        </Link>
        <Link to="/" style={NavbarStyles.iconStyles}>
          <PersonIcon />
        </Link>
      </Box>
    </Box>
  );
};

export default NavLinks;
