import React from "react";
import { FooterStyles } from "./FooterStyles";
import logo from "../../assets/whiteLogoBlankfactor.png";
import { FooterConstants } from "../../constants/FooterConstants"
import { useState } from "react";

const footerLinks = [
    { label: "About", href: FooterConstants.ABOUT_LINK },
    { label: "Services", href: FooterConstants.SERVICES_LINK },
    { label: "Careers", href: FooterConstants.CAREERS_LINK },
    { label: "Insights", href: FooterConstants.INSIGHTS_LINK },
    { label: "Privacy Policy", href: FooterConstants.PRIVACY_POLICY_LINK },
]

const Footer: React.FC = () => {
    const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);

    const handleMouseEnter = (index: number) => {
        setHoveredIndex(index);
      };
    
      const handleMouseLeave = () => {
        setHoveredIndex(null);
      };

  return (
    <footer style={FooterStyles.container}>
      <div style={FooterStyles.logoContainer}>
      <a href={FooterConstants.HOME_LINK} style={FooterStyles.logoLink} 
        target="_blank" rel="noopener noreferrer">
          <img src={logo} alt="Logo" style={FooterStyles.logo} />
        </a>
      </div>
      <nav style={FooterStyles.nav}>
        {footerLinks.map((link, index = 0) => (
          <a
            key={index}
            href={link.href}
            style={{
                ...FooterStyles.link,
                color: hoveredIndex === index ? "var(--secondary-color)" : "white",
            }}  
            onMouseEnter={() => handleMouseEnter(index)}
            onMouseLeave={handleMouseLeave}
            target="_blank"
            rel="noopener noreferrer"
          >
            {link.label}
          </a>
        ))}
      </nav>
    </footer>
  );
};

export default Footer;
