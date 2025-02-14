import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Navbar from "./components/navbar/Navbar";
import Footer from "./components/Footer/Footer";
import Login from "./pages/LoginPage/LoginPage";
import HomePage from "./pages/HomePage/HomePage";
import DashboardPage from "./pages/DashboardPage/DashboardPage";
import RegisterPage from "./pages/RegisterPage/RegisterPage";
import EventsPage from "./pages/EventsPage/EventsPage";
import TicketsPage from "./pages/TicketsPage/TicketsPage";
import NotFoundPage from "./pages/NotFoundPage/NotFoundPage";
import NotificationsPage from "./pages/NotificationsPage/NotificationsPage";
import CreateEventPage from "./pages/adminPages/CreateEventPage/CreateEventPage";
import CreateEventSucessPage from "./pages/adminPages/CreateEventSucessPage/CreateEventSucessPage";
import AccountPage from "./pages/adminPages/AccountPage/AccountPage";
import CreateVenuePage from "./pages/adminPages/CreateVenuePage/CreateVenuePage";
import CreateTicketPage from "./pages/adminPages/CreateTicketPage/CreateTicketPage";
import CssBaseline from "@mui/material/CssBaseline";
import "./App.scss";
import MockUserList from "./pages/MockUserList/MockUserList";
import { Box } from "@mui/material";
import SideBar from "./components/sidebar/SideBar";
import { useSelector } from "react-redux";
import { RootState } from "./redux/store";
import { AppStyles } from "./AppStyles";

export default function App() {
  const isSideBarOpen = useSelector((state: RootState) => state.sidebar.isOpen);

  return (
    <Router>
      <CssBaseline />
      <Navbar />
      <SideBar />
      <Box sx={AppStyles.outerBoxStyles}>
        <Box
          sx={{
            ...AppStyles.routesBoxStyles,
            marginLeft: isSideBarOpen ? "260px" : "80px",
          }}
        >
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/events" element={<EventsPage />} />
            <Route path="/tickets" element={<TicketsPage />} />
            <Route path="/notifications" element={<NotificationsPage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/not-found" element={<NotFoundPage />} />
            <Route path="/mock-users" element={<MockUserList />} />
            <Route path="*" element={<Navigate to="/not-found" replace />} />
            // Admin routes
            <Route
              path="/dashboard/event-creation/event"
              element={<CreateEventPage />}
            />
            <Route
              path="/dashboard/event-creation/venue"
              element={<CreateVenuePage />}
            />
            <Route
              path="/dashboard/event-creation/ticket"
              element={<CreateTicketPage />}
            />
            <Route
              path="/dashboard/event-creation/success"
              element={<CreateEventSucessPage />}
            />
            <Route path="/dashboard/your-events" />
            <Route path="/dashboard/your-events-statistics" />
            <Route path="/dashboard/rsvp-creation" />
            <Route path="/dashboard/account" element={<AccountPage />} />
          </Routes>
        </Box>
        <Footer />
      </Box>
    </Router>
  );
}
