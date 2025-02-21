import { FC, useState, FormEvent, ChangeEvent } from "react";
import { useDispatch, useSelector } from "react-redux";
import { forgotPassword, selectPasswordReset, showErrorMessage } from "../../redux/slices/passwordResetSlice";
import { AppDispatch } from "../../redux/store";
import "./ForgotPasswordPage.scss";
import { ForgotPasswordConstants } from "../../constants/AuthenticationConstants";

const ForgotPasswordPage: FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { loading, error, message, success } = useSelector(
    selectPasswordReset
  );
  const [email, setEmail] = useState<string>("");

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    setEmail(e.target.value);
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!email.trim()) {
        dispatch(
          showErrorMessage(ForgotPasswordConstants.ERROR_MISSING_EMAIL)
        );
        return;
      }

    dispatch(forgotPassword(email))
    .unwrap()
    .then(() => {
      })
    .catch((error) => {
        console.error(ForgotPasswordConstants.ERROR_SENDING_EMAIL, error);
      });
  };

  return (
    <div className="forgot-password-page">
      <h1>{ForgotPasswordConstants.FORGOT_PASSWORD_TITLE}</h1>
      <form onSubmit={handleSubmit} className="forgot-password-form">
        <div className="form-group">
          <label htmlFor="email">{ForgotPasswordConstants.EMAIL_LABEL}</label>
          <input
            type="email"
            id="email"
            name="email"
            placeholder={ForgotPasswordConstants.EMAIL_PLACEHOLDER}
            value={email}
            onChange={handleInputChange}
          />
        </div>
        {error && <p className="error">{error}</p>}
        {success && message && <p className="success">{message}</p>}
        <button type="submit" disabled={loading}>
          {loading ? ForgotPasswordConstants.SENDING_TEXT: ForgotPasswordConstants.SEND_RESET_LINK_TEXT}
        </button>
      </form>
    </div>
  );
};

export default ForgotPasswordPage;