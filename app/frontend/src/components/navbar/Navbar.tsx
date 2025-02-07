import {
  AppBar,
  Toolbar,
  Box,
  useTheme,
  useMediaQuery,
  Typography,
  IconButton,
  Menu,
} from "@mui/material";
import { Link,  } from "react-router-dom";
import MenuIcon from '@mui/icons-material/Menu';
import { NavbarStyles } from "./NavbarStyles";
import "./Navbar.scss";
import Logo from "../logo/Logo";
import { useState } from "react";
import NavLinks from "./NavLinks";

const Navbar = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const handleMenuOpen = (event: React.MouseEvent<any>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  return (
    <AppBar position="fixed" sx={NavbarStyles.navbarStyles}>
      <Toolbar disableGutters sx={NavbarStyles.toolbarStyles}>
        <Box sx={NavbarStyles.boxStyles}>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            <Link to="/" className="link-styles">
              <Logo />
            </Link>
          </Typography>

          {!isMobile && (
            <NavLinks flexDirection="row"/>
          )}

          {isMobile && (
            <>
              <IconButton color="inherit" onClick={handleMenuOpen}>
                <MenuIcon />
              </IconButton>
              <Menu
                anchorEl={anchorEl}
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
              >
                <NavLinks flexDirection="column" />
              </Menu>
            </>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
