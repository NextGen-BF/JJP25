import { IconButton, SxProps, Theme } from "@mui/material";
import { FC, ReactNode } from "react";

interface IconButtonComponentProps {
  sx?: SxProps<Theme>;
  onClick: () => void;
  icon: ReactNode
}

const IconButtonComponent: FC<IconButtonComponentProps> = ({ sx, onClick, icon }) => {
  return (
    <IconButton onClick={onClick} sx={sx}>
      {icon}
    </IconButton>
  );
};

export default IconButtonComponent;
