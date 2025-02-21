import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { resetPassword, selectPasswordReset, showErrorMessage } from "../../redux/slices/passwordResetSlice";
import { AppDispatch } from "../../redux/store";
import "./ResetPasswordPage.scss";
import { ResetPasswordConstants } from "../../constants/AuthenticationConstants";

const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,25}$/;

const ResetPasswordPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { loading, error, success } = useSelector(selectPasswordReset);
  const [formValues, setFormValues] = useState({
    newPassword: "",
    confirmPassword: "",
  });
  const [token, setToken] = useState("");
  const [touched, setTouched] = useState({
    newPassword: false,
    confirmPassword: false,
  }); 


  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const tokenFromUrl = params.get("token");
    if (tokenFromUrl) {
      setToken(tokenFromUrl);
      window.history.replaceState({}, document.title, window.location.pathname);
    }
  },);

  const getValidationError = (values: {
    newPassword: string;
    confirmPassword: string;
  }): string => {
    if (touched.newPassword &&!values.newPassword) {
      return ResetPasswordConstants.ERROR_MISSING_NEW_PASSWORD;
    }
    if (touched.confirmPassword &&!values.confirmPassword) {
      return ResetPasswordConstants.ERROR_MISSING_CONFIRM_PASSWORD;
    }
    if (
      touched.newPassword &&
      touched.confirmPassword &&
      values.newPassword!== values.confirmPassword
    ) {
      return ResetPasswordConstants.ERROR_PASSWORDS_DONT_MATCH;
    }
    if (touched.newPassword &&!PASSWORD_REGEX.test(values.newPassword)) {
      return ResetPasswordConstants.ERROR_INVALID_PASSWORD_FORMAT;
    }
    return "";
  };


  useEffect(() => {
    const validationError = getValidationError(formValues);
    if (validationError) {
      dispatch(showErrorMessage(validationError));
    } else {
      dispatch(showErrorMessage(null)); 
    }
  }, [formValues, touched]); 

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTouched({
    ...touched,
      [e.target.name]: true,
    }); 
    setFormValues({
    ...formValues,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setTouched({ newPassword: true, confirmPassword: true });

    const validationError = getValidationError(formValues);
    if (validationError) {
      dispatch(showErrorMessage(validationError));
      return;
    }

    dispatch(
      resetPassword({
        token,
        newPassword: formValues.newPassword,
        confirmPassword: formValues.confirmPassword,
      })
    )
    .unwrap()
    .then(() => {
        setTimeout(() => {
          navigate("/login");
        }, 2000);
      })
    .catch((error) => {
        console.error(ResetPasswordConstants.ERROR_RESETTING_PASSWORD, error);
      });
  };


  return (
    <div className="reset-password-page">
      <div className="reset-password-container">
        <h1>{ResetPasswordConstants.RESET_PASSWORD_TITLE}</h1>
        {error && <p className="error">{error}</p>}
        {success? (
          <p className="success-message">
            {ResetPasswordConstants.SUCCESS_MESSAGE}
          </p>
        ) : (
          <form className="reset-password-form" onSubmit={handleSubmit} noValidate>
            <div className="form-group">
              <label htmlFor="newPassword">{ResetPasswordConstants.NEW_PASSWORD_LABEL}</label>
              <input
                type="password"
                id="newPassword"
                name="newPassword"
                placeholder={ResetPasswordConstants.NEW_PASSWORD_PLACEHOLDER}
                value={formValues.newPassword}
                onChange={handleInputChange}
              />
            </div>
            <div className="form-group">
              <label htmlFor="confirmPassword">{ResetPasswordConstants.CONFIRM_PASSWORD_LABEL}</label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                placeholder={ResetPasswordConstants.CONFIRM_PASSWORD_PLACEHOLDER}
                value={formValues.confirmPassword}
                onChange={handleInputChange}
              />
            </div>
            <button type="submit" className="reset-button" disabled={loading}>
              {loading ? ResetPasswordConstants.RESETTING_BUTTON_TEXT : ResetPasswordConstants.RESET_BUTTON_TEXT}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default ResetPasswordPage;
