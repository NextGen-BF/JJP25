import {
  Drawer,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import { FC } from "react";
import ViewSidebarIcon from "@mui/icons-material/ViewSidebarOutlined";
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../../redux/store";
import { toggleSidebarStatus } from "../../redux/slices/sidebarSlice";
import { SideBarStyles } from "./SideBarStyles";;
import IconButtonComponent from "../IconButton/IconButtonComponent";
import DrawerContent from "./DrawerContent/DrawerContent";

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
        <DrawerContent />
      </Drawer>
    </>
  );
};

export default SideBar;
