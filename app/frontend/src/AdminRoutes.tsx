import { Route } from "react-router-dom";
import AccountPage from "./pages/adminPages/AccountPage/AccountPage";
import CreateEventSucessPage from "./pages/adminPages/CreateEventSucessPage/CreateEventSucessPage";
import RSVPCreatePage from "./pages/RSVPCreatePage/RSVPCreatePage";
import CreateEventPage from "./pages/adminPages/CreateEventPage/CreateEventPage";

const AdminRoutes = (
  <>
    <Route path="/dashboard/event-creation" element={<CreateEventPage />} />
    <Route
      path="/dashboard/event-creation/success"
      element={<CreateEventSucessPage />}
    />
    <Route path="/dashboard/your-events" />
    <Route path="/dashboard/your-events-statistics" />
    <Route path="/dashboard/rsvp-creation" element={<RSVPCreatePage />} />
    <Route path="/dashboard/account" element={<AccountPage />} />
  </>
);

export default AdminRoutes;
