import { ElementType, FC } from "react";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import { Box, ListSubheader, SvgIconProps } from "@mui/material";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { SideBarStyles } from "./SideBarStyles";
import { NavLink } from "react-router-dom";
import "./SideBar.scss"

export interface SidebarItem {
  icon: ElementType<SvgIconProps>;
  message: string;
  linkTo: string;
}

interface SidebarProps {
  items: SidebarItem[];
  title: string;
}

export const SideSection: FC<SidebarProps> = ({ items, title }) => {
  const isOpen = useSelector((state: RootState) => state.sidebar.isOpen);

  const toggleClass = (isActive : boolean): string => {
    if (isActive) {
      return isOpen ? 
      "link-active link-open" : 
      "link-active link-closed";
    } 

    return "link-styles";
  }

  return (
    <Box>
      <ListSubheader

        sx={{...SideBarStyles.listSubheaderStyles, fontSize: isOpen ? 25 : 16, paddingLeft: 1}}
      >
        {title}
      </ListSubheader>
      <List>
        {items.map((item, index) => (
          <NavLink to={item.linkTo} className={({ isActive }) => toggleClass(isActive)} >
            <ListItem key={index} disablePadding >
              <ListItemButton disableRipple className="list-item-button">
                <ListItemIcon >
                  <item.icon />
                </ListItemIcon>
                {isOpen && (
                  <ListItemText
                    primary={item.message}
                    sx={SideBarStyles.listItemTextStyles}
                  />
                )}
              </ListItemButton>
            </ListItem>
          </NavLink>
        ))}
      </List>
    </Box>
  );
};
