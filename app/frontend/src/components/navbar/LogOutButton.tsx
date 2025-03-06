//Material UI
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import LogoutIcon from "@mui/icons-material/Logout";

//React
import { FC, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

//Firebase Context
import { UserAuth } from "../../fireabase_context/AuthContext";
import { NavbarStyles } from "./NavbarStyles";

const LogOutButton: FC = () => {
  const [open, setOpen] = useState<boolean>(false);
  const { signOutFromGoogle } = UserAuth();
  const navigate = useNavigate();

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleConfirmSignOut = () => {
    signOutFromGoogle();
    setOpen(false);
    navigate("/login");
  };

  return (
    <>
      <Link to="#"
        style={NavbarStyles.iconStyles}
        onClick={handleClickOpen}
      >
        <LogoutIcon />
      </Link>
      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Confirm Sign Out"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure you want to sign out?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleConfirmSignOut} color="primary" autoFocus>
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default LogOutButton;
