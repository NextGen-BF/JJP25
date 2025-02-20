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
import CssBaseline from "@mui/material/CssBaseline";
import "./App.scss";
import MockUserList from "./pages/MockUserList/MockUserList";
import { Box } from "@mui/material";
import RSVPCreatePage from "./pages/RSVPCreatePage/RSVPCreatePage";
import { ToastContainer } from "react-toastify";
import { AppStyles } from "./AppStyles";

export default function App() {
  return (
    <Router>
      <CssBaseline />
      <ToastContainer />
      <Box sx={AppStyles.outerBoxStyles}>
        <Navbar />

        <Box sx={AppStyles.routesBoxStyles}>
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
              path="/dashboard/event-creation"
              element={<CreateEventPage />}
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
