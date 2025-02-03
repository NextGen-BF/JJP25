import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar  from './components/Navbar';
import Login from './pages/LoginPage/LoginPage';
import HomePage from './pages/HomePage/HomePage';
import DashboardPage from './pages/DashboardPage/DashboardPage';
import RegisterPage from './pages/RegisterPage/RegisterPage';
import EventsPage from './pages/EventsPage/EventsPage';
import TicketsPage from './pages/TicketsPage/TicketsPage';
import NotFoundPage from './pages/NotFoundPage/NotFoundPage';
import NotificationsPage from './pages/NotificationsPage/NotificationsPage';
import CssBaseline from '@mui/material/CssBaseline';
import './App.scss'

export default function App() {
  return (
    <Router>
      <CssBaseline />
    <Navbar />
      <div className='routes-container' >
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/events" element={<EventsPage />} />
          <Route path="/tickets" element={<TicketsPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/not-found" element={<NotFoundPage />} />
          <Route path="*" element={<Navigate to="/not-found" replace />} />
        </Routes>
      </div>
    </Router>
  );
}
