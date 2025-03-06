//React
import { FC, ReactNode, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

//Redux
import { useSelector } from "react-redux";
import { RootState } from "../redux/store";

//Toastify
import { toast } from "react-toastify";
import { SelectRoleConstants } from "../constants/SelectRoleConstants";

//Material
import { CircularProgress } from "@mui/material";

interface AuthRedirectProps {
  children: ReactNode;
}

const AuthRedirect: FC<AuthRedirectProps> = ({ children }) => {
  const navigate = useNavigate();
  const { user } = useSelector((state: RootState) => state.googleAuth);
  const [loading, setLoading] = useState<boolean>(false);

  const handleLoading = () => {
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
    }, 500);
  };

  useEffect(() => {
    if (!user) {
      handleLoading();

      navigate(SelectRoleConstants.URLs.LOGIN_URL);
    } else if (!user.role) {
      handleLoading();

      toast.info(
        SelectRoleConstants.SUCCESS_MESSAGES.WELCOME_MESSAGE(user.name)
      );

      navigate(SelectRoleConstants.URLs.SELECT_ROLE_URL);
    } else if (user && user.role) {
      handleLoading();

      toast.success(
        SelectRoleConstants.SUCCESS_MESSAGES.WELCOME_BACK_MESSAGE(user.name)
      );

      navigate(SelectRoleConstants.URLs.DASHBOARD_URL);
    }
  }, [user]);

  return (
    <>
      {loading ? (
        <CircularProgress
          size={100}
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            margin: "auto",
          }}
        />
      ) : (
        children
      )}{" "}
    </>
  );
};

export default AuthRedirect;
