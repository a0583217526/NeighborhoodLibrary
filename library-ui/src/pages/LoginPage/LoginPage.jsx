import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login, loginWithGoogle } from "../../api/authApi";
import "./LoginPage.css";

function LoginPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    try {
      await login(email, password);
      navigate("/main");
    } catch (err) {
      alert("אימייל או סיסמה שגויים");
    }
  };

  return (
    <div className="auth-container">

      <div className="auth-card">

        <h1 className="auth-title">ספריית השכונה</h1>

        <input
          className="auth-input"
          type="email"
          placeholder="אימייל"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          className="auth-input"
          type="password"
          placeholder="סיסמה"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button className="auth-button primary" onClick={handleLogin}>
          כניסה
        </button>

        <div className="divider">או</div>

        {/* Google Button */}
        <button className="google-button" onClick={loginWithGoogle}>
          <img
            src="https://developers.google.com/identity/images/g-logo.png"
            alt="google"
          />
          התחברות עם Google
        </button>

        <p className="switch-text">
          אין לך חשבון?
          <span onClick={() => navigate("/register")}> הירשם כאן</span>
        </p>

      </div>
    </div>
  );
}

export default LoginPage;