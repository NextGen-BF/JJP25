import { FC, useState, FormEvent, ChangeEvent } from "react";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import "./LoginPage.scss";
import sideImage from "../../assets/side-image.png";
import gmailLogo from "../../assets/google-color.png";

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
  const [formValues, setFormValues] = useState<FormValues>({
    username: '',
    password: '',
    rememberMe: false,
  });
  const [errors, setErrors] = useState<Errors>({ username: '', password: '' });
  const [loginError, setLoginError] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const navigate = useNavigate();

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormValues({
      ...formValues,
      [name]: type === 'checkbox' ? checked : value,
    });
  };

  const validateForm = (): boolean => {
    let valid = true;
    const newErrors: Errors = { username: '', password: '' };
    if (!formValues.username) {
      newErrors.username = 'Please enter your email or username';
      valid = false;
    }
    if (!formValues.password) {
      newErrors.password = 'Please enter your password';
      valid = false;
    }
    setErrors(newErrors);
    return valid;
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoginError('');
    if (!validateForm()) return;
    setLoading(true);
    try {
      const response = await axios.post(
        'http://localhost:8081/api/v1/auth/login',
        {
          loginIdentifier: formValues.username,
          password: formValues.password,
        }
      );
      const { token } = response.data;
      localStorage.setItem('token', token);
      navigate('/dashboard');
    } catch (err) {
      const errorMessage = axios.isAxiosError(err)
        ? err.response?.data?.message
        : null;
    
      setLoginError(errorMessage || 'An unexpected error occurred.');
    } finally {
      setLoading(false);
    }
  };

  const handleGmailLogin = () => {
    // Placeholder for Gmail login action
    alert('Gmail login clicked (to be implemented)');
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="side-image">
          <img src={sideImage} alt="Side Visual" />
        </div>

        <div className="login-form-container">
          <div className="login-form-top">
            <h1>Login</h1>
            <div className="error-container">
              {loginError && <p className="error">{loginError}</p>}
            </div>
            <form className="login-form" onSubmit={handleSubmit} noValidate>
              <div className="form-group">
                <label htmlFor="username">Email/Username</label>
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={formValues.username}
                  onChange={handleInputChange}
                  placeholder="Enter your email or username"
                />
                {errors.username && (
                  <span className="error">{errors.username}</span>
                )}
              </div>
              <div className="form-group">
                <label htmlFor="password">Password</label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formValues.password}
                  onChange={handleInputChange}
                  placeholder="Enter your password"
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
                <label htmlFor="rememberMe">Remember Me</label>
              </div>
              <button type="submit" className="login-button" disabled={loading}>
                {loading ? "Logging in..." : "Login"}
              </button>
            </form>
            <div className="or-separator">or</div>
            <div className="gmail-login">
              <button onClick={handleGmailLogin} className="gmail-button">
                <img src={gmailLogo} alt="Gmail Logo" className="gmail-logo" />
                <span>Login with Gmail</span>
              </button>
            </div>
          </div>
          <div className="login-form-bottom">
            <p className="signup-prompt">
              Don't have an account? <Link to="/register">Sign up!</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );  
}  
export default LoginPage;
