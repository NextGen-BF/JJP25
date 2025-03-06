import { FC } from "react";
import { UserAuth } from "../../fireabase_context/AuthContext";
import { toast } from "react-toastify";
import { LoginPageConstants } from "../../constants/LoginPageConstants";
import gmailLogo from "../../assets/google-color.png"
import { SelectRoleConstants } from "../../constants/SelectRoleConstants";

const GoogleLoginButton: FC = () => {
  const { signInWithGoogle } = UserAuth();

  const handleGoogleLogin = async () => {
    try {
      await signInWithGoogle();
    } catch (error) {
      toast.error(SelectRoleConstants.ERROR_MESSAGES.ERROR_OCCURED);
    }
  };

  return (
    <div className="gmail-login">
      <button onClick={handleGoogleLogin} className="gmail-button">
        <img src={gmailLogo} alt="Gmail Logo" className="gmail-logo" />
        <span>{LoginPageConstants.GMAIL_LOGIN_TEXT}</span>
      </button>
    </div>
  );
};

export default GoogleLoginButton;
