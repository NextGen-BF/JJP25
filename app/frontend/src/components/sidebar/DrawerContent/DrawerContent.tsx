import { Box, IconButton } from "@mui/material";
import { FC } from "react";
import { SideSection } from "../SideSection";
import {
  sidebarItemsAccount,
  sidebarItemsEvents,
  sidebarItemsRSVPs,
} from "../../../constants/SidebarConstants";
import ViewSidebarIcon from "@mui/icons-material/ViewSidebarOutlined";
import { useDispatch } from "react-redux";
import { AppDispatch } from "../../../redux/store";
import { toggleSidebarStatus } from "../../../redux/slices/sidebarSlice";
import { drawerContentStyles } from "./DrawerContentStyles";

const DrawerContent: FC = () => {
  const dispatch = useDispatch<AppDispatch>();

  const toggleDrawer = () => {
    dispatch(toggleSidebarStatus());
  };

  return (
    <Box sx={drawerContentStyles.drawerStyles}>
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
};

export default DrawerContent;
