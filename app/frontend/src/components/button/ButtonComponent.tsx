import { FC } from "react";
import Button from "@mui/material/Button";
import ButtonStyles from "./ButtonComponentStyles";
import { buttonColors } from "../../constants/ButtonConstants";

interface ButtonProps {
  label: string;
  size?: "small" | "medium" | "large";
  color: "primary" | "secondary" | "error";
  hoverColor?: "primary" | "secondary" | "error" ;
  onClickHandler: () => void;
}

const ButtonComponent: FC<ButtonProps> = ({
  label,
  size,
  color,
  hoverColor,
  onClickHandler,
}) => {

  const getHoverColor = () => hoverColor ? buttonColors[hoverColor] : "";

  return (
    <Button
      disableRipple
      disableFocusRipple
      size={size}
      sx={{ ...ButtonStyles, backgroundColor: buttonColors[color], 
        ":hover": {
          backgroundColor: getHoverColor()
        } 
      }}
      variant="contained"
      onClick={onClickHandler}
    >
      {label}
    </Button>
  );

};

export default ButtonComponent;
