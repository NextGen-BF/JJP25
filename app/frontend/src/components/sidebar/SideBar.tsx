import {
  Box,
  Drawer,
  IconButton,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { FC } from "react";
import ViewSidebarIcon from "@mui/icons-material/ViewSidebarOutlined";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../../redux/store";
import { toggleSidebarStatus } from "../../redux/slices/sidebarSlice";
import { SideSection } from "./SideSection";
import { SideBarStyles } from "./SideBarStyles";
import {
  sidebarItemsAccount,
  sidebarItemsEvents,
  sidebarItemsRSVPs,
} from "../../constants/SidebarConstants";
import IconButtonComponent from "../IconButton/IconButtonComponent";

export const SideBar: FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const isOpen = useSelector((state: RootState) => state.sidebar.isOpen);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const collapsedWidth = 80;
  const expandedPCWidth = 240;

  const expandedWidth = isMobile ? "100%" : expandedPCWidth;

  const toggleDrawer = () => {
    dispatch(toggleSidebarStatus());
  };

  const DrawerContent = (
    <Box sx={SideBarStyles.drawerContentBoxStyles}>
      <Box sx={SideBarStyles.drawerContentInnerBoxStyles}>
        <IconButton onClick={toggleDrawer} sx={{ mr: 100 }}>
          <ViewSidebarIcon />
        </IconButton>
        <IconButtonComponent
          onClick={toggleDrawer}
          sx={{ mr: 100 }}
          icon={<ViewSidebarIcon />}
        />
      </Box>
      <SideSection items={sidebarItemsEvents} title="Event" />
      <SideSection items={sidebarItemsRSVPs} title="RSVPs" />
      <SideSection items={sidebarItemsAccount} title="Account" />
    </Box>
  );

  return (
    <>
      {isMobile && (
        <IconButtonComponent
          onClick={toggleDrawer}
          sx={SideBarStyles.iconButtonStyles}
          icon={<ViewSidebarIcon />}
        />
      ) }
      <Drawer
        variant="permanent"
        open={isOpen}
        sx={{
          "& .MuiDrawer-paper": {
            width: isOpen ? expandedWidth : 
            isMobile ? 0 : collapsedWidth,
            ...SideBarStyles.drawerStyles,
          },
        }}
      >
        {DrawerContent}
      </Drawer>
    </>
  );
};

export default SideBar;
