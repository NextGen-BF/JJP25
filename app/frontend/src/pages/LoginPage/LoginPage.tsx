import { FC, useState, FormEvent, ChangeEvent } from 'react';
import { Link } from 'react-router-dom';
import './LoginPage.scss';
import sideImage from '../../assets/side-image.png';     
import gmailLogo from '../../assets/google-color.png';         

const LoginPage: FC = () => {
  const [formValues, setFormValues] = useState({
    username: '',
    password: '',
    rememberMe: false,
  });
  const [errors, setErrors] = useState({
    username: '',
    password: '',
  });

  const handleInputChange = (e: ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormValues({
      ...formValues,
      [name]: type === 'checkbox' ? checked : value,
    });
  };

  const validate = () => {
    let valid = true;
    const newErrors = { username: '', password: '' };

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

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (validate()) {
      // Placeholder for login action
      alert('Login successful (to be implemented)!');
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
          <h1>Login</h1>
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
              {errors.username && <span className="error">{errors.username}</span>}
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
              {errors.password && <span className="error">{errors.password}</span>}
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
            <button type="submit" className="login-button">
              Login
            </button>
          </form>

          <div className="gmail-login">
            <button onClick={handleGmailLogin} className="gmail-button">
              <img src={gmailLogo} alt="Gmail Logo" className="gmail-logo" />
              <span>Login with Gmail</span>
            </button>
          </div>

          <p className="signup-prompt">
            Don't have an account? <Link to="/register">Sign up!</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
