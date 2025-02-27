import { GoogleOAuthProvider, GoogleLogin, CredentialResponse } from "@react-oauth/google";
import axios from "axios";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

const clientId = "449311094395-e0nhgs5m3of6lk6e045terrji11acnv8.apps.googleusercontent.com";

function GmailButton() {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleGoogleLogin = async (response: CredentialResponse) => {
        setLoading(true);
        setError("");

        console.log(response.credential);
        
        if (response.credential) {
            try {
                const res = await axios.post("http://localhost:8081/auth/google", {
                    token: response.credential,
                }, {
                    headers: { "Content-Type": "application/json" },
                    withCredentials: true,
                });

                console.log("Backend response:", res.data);
                localStorage.setItem("token", res.data.token); 

                navigate("/dashboard"); 
            } catch (error) {
                console.error("Authentication failed:", error);
                setError("Google login failed. Please try again.");
            }
        }
        
        setLoading(false);
    };

    return (
        <GoogleOAuthProvider clientId={clientId}>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <GoogleLogin
                    onSuccess={handleGoogleLogin}
                    onError={() => setError("Google login failed. Try again.")}
                />
            )}
            {error && <p style={{ color: "red" }}>{error}</p>}
        </GoogleOAuthProvider>
    );
}

export default GmailButton;