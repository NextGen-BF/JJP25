import { Navigate, Route } from "react-router-dom";
import HomePage from "./pages/HomePage/HomePage";
import RegisterPage from "./pages/RegisterPage/RegisterPage";
import DashboardPage from "./pages/DashboardPage/DashboardPage";
import EventsPage from "./pages/EventsPage/EventsPage";
import MockUserList from "./pages/MockUserList/MockUserList";
import NotFoundPage from "./pages/NotFoundPage/NotFoundPage";
import NotificationsPage from "./pages/NotificationsPage/NotificationsPage";
import TicketsPage from "./pages/TicketsPage/TicketsPage";
import LoginPage from "./pages/LoginPage/LoginPage";

const UserRoutes = (
    <>
      <Route path="/" element={<HomePage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/events" element={<EventsPage />} />
      <Route path="/tickets" element={<TicketsPage />} />
      <Route path="/notifications" element={<NotificationsPage />} />
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/not-found" element={<NotFoundPage />} />
      <Route path="/mock-users" element={<MockUserList />} />
      <Route path="*" element={<Navigate to="/not-found" replace />} />
    </>
  );

export default UserRoutes;
