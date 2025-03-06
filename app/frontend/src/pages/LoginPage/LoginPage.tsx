import { FC, useState, FormEvent, ChangeEvent } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  loginUser,
  selectAuth,
  clearError,
} from "../../redux/slices/authSlice";
import { AppDispatch } from "../../redux/store";
import "./LoginPage.scss";
import sideImage from "../../assets/side-image.png";
import { LoginPageConstants } from "../../constants/AuthenticationConstants";
import GoogleLoginButton from "./GmailButton";

interface FormValues {
  username: string;
  password: string;
  rememberMe: boolean;
}

interface Errors {
  username: string;
  password: string;
}

const LoginPage: FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { loading, error } = useSelector(selectAuth);
  const [formValues, setFormValues] = useState<FormValues>({
    username: "",
    password: "",
    rememberMe: false,
  });
  const [errors, setErrors] = useState<Errors>({ username: "", password: "" });

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormValues({
      ...formValues,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  const validateForm = (): boolean => {
    let valid = true;
    const newErrors: Errors = { username: "", password: "" };
    if (!formValues.username) {
      newErrors.username = LoginPageConstants.ERROR_MISSING_IDENTIFIER;
      valid = false;
    }
    if (!formValues.password) {
      newErrors.password = LoginPageConstants.ERROR_MISSING_PASSWORD;
      valid = false;
    }
    setErrors(newErrors);
    return valid;
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    dispatch(clearError());
    if (!validateForm()) return;

    dispatch(loginUser(formValues))
      .unwrap()
      .then(() => navigate("/dashboard"))
      .catch((error: any) => {
        console.error(LoginPageConstants.LOGIN_FAILED, error);
      });
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="side-image">
          <img src={sideImage} alt="Side Visual" />
        </div>

        <div className="login-form-container">
          <div className="login-form-top">
            <h1>{LoginPageConstants.LOGIN_TEXT}</h1>
            <div className="error-container">
              {error && <p className="error">{error}</p>}
            </div>
            <form className="login-form" onSubmit={handleSubmit} noValidate>
              <div className="form-group">
                <label htmlFor="username">
                  {LoginPageConstants.EMAIL_USERNAME_LABEL}
                </label>
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={formValues.username}
                  onChange={handleInputChange}
                  placeholder={LoginPageConstants.PLACEHOLDER_FOR_IDENTIFIER}
                />
                {errors.username && (
                  <span className="error">{errors.username}</span>
                )}
              </div>
              <div className="form-group">
                <label htmlFor="password">
                  {LoginPageConstants.PASSWORD_LABEL}
                </label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formValues.password}
                  onChange={handleInputChange}
                  placeholder={LoginPageConstants.PLACEHOLDER_FOR_PASSWORD}
                />
                {errors.password && (
                  <span className="error">{errors.password}</span>
                )}
              </div>
              <div className="form-group remember-me">
                <input
                  type="checkbox"
                  id="rememberMe"
                  name="rememberMe"
                  checked={formValues.rememberMe}
                  onChange={handleInputChange}
                />
                <label htmlFor="rememberMe">
                  {LoginPageConstants.REMEMBER_ME_LABEL}
                </label>
              </div>
              <button type="submit" className="login-button" disabled={loading}>
                {loading
                  ? LoginPageConstants.LOGGING_IN
                  : LoginPageConstants.LOGIN_TEXT}
              </button>
            </form>
            <div className="or-separator">
              {LoginPageConstants.OR_SEPARATOR}
            </div>
            <GoogleLoginButton />
          </div>
          <div className="login-form-bottom">
            <p className="signup-prompt">
              {LoginPageConstants.SIGNUP_PROMPT} <Link to="/register">{LoginPageConstants.SIGNUP_TEXT}</Link>
            </p>
            <p className="forgot-password-link">
              <Link to="/forgot-password">{LoginPageConstants.FORGOT_PASSWORD_PROMPT}</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
export default LoginPage;
