import { SidebarItem } from "../components/sidebar/SideSection";
import AddBoxIcon from "@mui/icons-material/AddBox";
import SignalCellularAltIcon from "@mui/icons-material/SignalCellularAlt";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";
import CalendarTodayOutlinedIcon from "@mui/icons-material/CalendarTodayOutlined";

export const expandedPCWidth = 240;
export const collapsedWidth = 80;

export const sidebarItemsEvents: SidebarItem[] = [
  {
    icon: AddBoxIcon,
    message: "Create Event",
    linkTo: "/create-event",
  },
  {
    icon: CalendarTodayOutlinedIcon,
    message: "Check Your Events",
    linkTo: "/check-events",
  },
  {
    icon: SignalCellularAltIcon,
    message: "Event Statistics",
    linkTo: "/statistics",
  },
];

export const sidebarItemsRSVPs: SidebarItem[] = [
  {
    icon: MailOutlineIcon,
    message: "Send RSVP",
    linkTo: "/rsvp",
  },
];

export const sidebarItemsAccount: SidebarItem[] = [
  {
    icon: AttachMoneyIcon,
    message: "Check Account",
    linkTo: "/account",
  },
];
