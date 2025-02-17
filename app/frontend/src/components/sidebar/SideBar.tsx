import {
  Box,
  Drawer,
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
import IconButtonComponent from "../IconButtonComponent/IconButtonComponent";

export const SideBar: FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const isOpen = useSelector((state: RootState) => state.sidebar.isOpen);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const collapsedWidth = isMobile ? 0 : 80;
  const expandedPCWidth = 240;

  const expandedWidth = isMobile ? "100%" : expandedPCWidth;

  const toggleDrawer = () => {
    dispatch(toggleSidebarStatus());
  };

  const DrawerContent = (
    <Box sx={SideBarStyles.drawerContentBoxStyles}>
      <Box sx={SideBarStyles.drawerContentInnerBoxStyles}>
        <IconButtonComponent
          onClick={toggleDrawer}
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
          sx={SideBarStyles.iconButtonStyles}
          onClick={toggleDrawer}
          icon={<ViewSidebarIcon />}
        />
      )}
      <Drawer
        variant="permanent"
        open={isOpen}
        sx={{
          "& .MuiDrawer-paper": {
            width: isOpen ? expandedWidth : collapsedWidth,
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
