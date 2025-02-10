import {
  Box,
  Drawer,
  IconButton,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import CalendarTodayOutlinedIcon from "@mui/icons-material/CalendarTodayOutlined";
import { FC } from "react";
import ViewSidebarIcon from "@mui/icons-material/ViewSidebarOutlined";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../../redux/store";
import { toggleSidebar } from "../../redux/slices/sidebarSlice";
import { SidebarItem, SideSection } from "./SideSection";
import AddBoxIcon from "@mui/icons-material/AddBox";
import SignalCellularAltIcon from "@mui/icons-material/SignalCellularAlt";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";

export const SideBar: FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const isOpen = useSelector((state: RootState) => state.sidebar.isOpen);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const expandedWidth = isMobile ? "100%" : 240;
  const collapsedWidth = 80;

  const toggleDrawer = () => {
    dispatch(toggleSidebar());
  };

  const sidebarItemsEvents: SidebarItem[] = [
    { icon: AddBoxIcon, message: "Create Event", linkTo: "/create-event" },
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

  const sidebarItemsRSVPs: SidebarItem[] = [
    { icon: MailOutlineIcon, message: "Send RSVP", linkTo: "/rsvp" },
  ];

  const sidebarItemsAccount: SidebarItem[] = [
    { icon: AttachMoneyIcon, message: "Check Account", linkTo: "/account" },
  ];

  const DrawerContent = (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        height: "110vh",
        bgcolor: "#D2D2DA",
        overflow: "hidden",
      }}
    >
      <Box sx={{ display: "flex", justifyContent: "flex-start", p: 1.5 }}>
        <IconButton onClick={toggleDrawer} sx={{ mr: 100 }}>
          <ViewSidebarIcon />
        </IconButton>
      </Box>
      <SideSection items={sidebarItemsEvents} title="Event" />
      <SideSection items={sidebarItemsRSVPs} title="RSVPs" />
      <SideSection items={sidebarItemsAccount} title="Account" />
    </Box>
  );

  return (
    <Drawer
      variant="permanent"
      open={isOpen}
      sx={{
        width: isOpen ? expandedWidth : collapsedWidth,
        flexShrink: 0,
        "& .MuiDrawer-paper": {
          width: isOpen ? expandedWidth : collapsedWidth,
          boxSizing: "border-box",
          transition: "width 0.3s",
          top: 64,
          height: `calc(100% - ${64}px)`,
        },
      }}
    >
      {DrawerContent}
    </Drawer>
  );
};

export default SideBar;
