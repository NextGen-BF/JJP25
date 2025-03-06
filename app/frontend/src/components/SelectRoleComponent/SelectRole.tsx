//React
import { useNavigate } from "react-router-dom";

//Material UI
import {
  Box,
  Button,
  Typography,
  ToggleButton,
  ToggleButtonGroup,
  Paper,
  CircularProgress,
} from "@mui/material";

//Additional for component
import { SelectRoleStyles } from "./SelectRoleStyles";
import { SelectRoleConstants } from "../../constants/SelectRoleConstants";

//Toastify
import { toast } from "react-toastify";

//Redux
import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../../redux/store";
import { setLoading, setRole } from "../../redux/slices/googleAuthSlice";

//Firebase Auth Context
import { doc, setDoc } from "firebase/firestore";
import { db } from "../../firebase/firebase";

export type UserRole = "ATTENDEE" | "ORGANIZER" | null;

const SelectRole = () => {
  const navigate = useNavigate();
  const { user, role, loading } = useSelector(
    (state: RootState) => state.googleAuth
  );
  const dispatch = useDispatch<AppDispatch>();

  const handleRoleChange = (
    _event: React.MouseEvent<HTMLElement>,
    newRole: UserRole
  ) => {
    if (newRole !== null) {
      dispatch(setRole(newRole));
    }
  };

  const handleSubmit = async () => {
    if (!user || !role) return;

    try {
      dispatch(setLoading(true));
      const userRef = doc(
        db,
        SelectRoleConstants.DATABASE.USERS_COLLECTION,
        user.uid
      );

      await setDoc(userRef, {
        name: user.name,
        email: user.email,
        role,
      });

      dispatch(setRole(role));

      toast.success(
        SelectRoleConstants.SUCCESS_MESSAGES.ROLE_ASSIGNED_SUCCESSFULLY
      );
      navigate(SelectRoleConstants.URLs.DASHBOARD_URL);
    } catch (error) {
      toast.error(SelectRoleConstants.ERROR_MESSAGES.ERROR_OCCURED);
      console.error(error);
    }
  };

  return (
    <Box sx={SelectRoleStyles.outerBoxStyles}>
      <Paper elevation={3} sx={SelectRoleStyles.paperStyles}>
        <Typography sx={SelectRoleStyles.typographyStyles}>
          Choose Your Role
        </Typography>

        <ToggleButtonGroup
          color="primary"
          value={role}
          exclusive
          onChange={handleRoleChange}
          fullWidth
        >
          <ToggleButton value={SelectRoleConstants.ROLES.ATTENDEE}>
            Attendee
          </ToggleButton>
          <ToggleButton value={SelectRoleConstants.ROLES.ORGANIZER}>
            Organizer
          </ToggleButton>
        </ToggleButtonGroup>

        <Button
          variant="contained"
          color="primary"
          fullWidth
          sx={{ mt: 3 }}
          onClick={handleSubmit}
          disabled={!role || loading}
        >
          {loading ? (
            <CircularProgress size={24} />
          ) : (
            SelectRoleConstants.LABELS.CONFIRM_ROLE
          )}
        </Button>
      </Paper>
    </Box>
  );
};

export default SelectRole;
