import { BrowserRouter as Router } from "react-router-dom";
import Navbar from "./components/navbar/Navbar";
import Footer from "./components/Footer/Footer";
import CssBaseline from "@mui/material/CssBaseline";
import "./App.scss";
import { Box, useMediaQuery, useTheme } from "@mui/material";
import SideBar from "./components/sidebar/SideBar";
import { useSelector } from "react-redux";
import { RootState } from "./redux/store";
import { AppStyles } from "./AppStyles";
import AppRoutes from "./AppRoutes";
import { ToastContainer } from "react-toastify";

export default function App() {
  const isSideBarOpen = useSelector((state: RootState) => state.sidebar.isOpen);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  return (
    <Router>
      <ToastContainer />
      <CssBaseline />
      <Navbar />
      <Box sx={AppStyles.outerBoxStyles}>
        <SideBar />
        <Box
          sx={{
            ...AppStyles.routesBoxStyles,
            marginLeft: isSideBarOpen ? "260px" : isMobile ? 0 : "80px",
          }}
        >
          <AppRoutes />
        </Box>
        <Footer />
      </Box>
    </Router>
  );
}
