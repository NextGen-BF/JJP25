import {
  AppBar,
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
import { FC, useState } from "react";
import NavLinks from "./NavLinks";
import Logo from "../../assets/logo.svg"

const Navbar: FC = () => {
  const [anchorEl, setAnchorEl] = useState<HTMLButtonElement | null>();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const handleMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  return (
    <AppBar position="fixed" sx={NavbarStyles.navbarStyles}>
      <Box sx={NavbarStyles.toolbarStyles}>
        <Box sx={NavbarStyles.boxStyles}>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            <Link to="/" className="link-styles">
              <img src={Logo} /> 
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
      </Box>
    </AppBar>
  );
};

export default Navbar;
