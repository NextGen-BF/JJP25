import React, { useState } from "react";
import logo from "../../assets/whiteLogoBlankfactor.png";
import { FooterConstants } from "../../constants/FooterConstants";
import "./footerStyles.scss";

const footerLinks = [
  { label: "About", href: FooterConstants.ABOUT_LINK },
  { label: "Services", href: FooterConstants.SERVICES_LINK },
  { label: "Careers", href: FooterConstants.CAREERS_LINK },
  { label: "Insights", href: FooterConstants.INSIGHTS_LINK },
  { label: "Privacy Policy", href: FooterConstants.PRIVACY_POLICY_LINK },
];

const Footer: React.FC = () => {
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

  const handleMouseEnter = (index: number) => {
    setHoveredIndex(index);
  };

  const handleMouseLeave = () => {
    setHoveredIndex(null);
  };

  return (
    <footer className="footer-container">
      <div className="footer-logo-container">
        <a
          href={FooterConstants.HOME_LINK}
          className="footer-logo-link"
          target="_blank"
          rel="noopener noreferrer"
        >
          <img src={logo} alt="Logo" className="footer-logo" />
        </a>
      </div>

      {/* Come up with a better solution to avoid mapping twice */}
      <nav className="footer-nav">
        <div className="footer-nav-group">
          {footerLinks.slice(0, 3).map((link, index) => (
            <a
              key={index}
              href={link.href}
              className={`footer-link ${
                hoveredIndex === index ? "footer-link-hover" : ""
              }`}
              onMouseEnter={() => handleMouseEnter(index)}
              onMouseLeave={handleMouseLeave}
              target="_blank"
              rel="noopener noreferrer"
            >
              {link.label}
            </a>
          ))}
        </div>

        <div className="footer-nav-group">
          {footerLinks.slice(3).map((link, index) => (
            <a
              key={index + 3} // Offset index to keep unique keys
              href={link.href}
              className={`footer-link ${
                hoveredIndex === index + 3 ? "footer-link-hover" : ""
              }`}
              onMouseEnter={() => handleMouseEnter(index + 3)}
              onMouseLeave={handleMouseLeave}
              target="_blank"
              rel="noopener noreferrer"
            >
              {link.label}
            </a>
          ))}
        </div>
      </nav>
    </footer>
  );
};

export default Footer;
