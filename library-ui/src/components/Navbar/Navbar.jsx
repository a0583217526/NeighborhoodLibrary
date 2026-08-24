import { useNavigate, useLocation } from "react-router-dom"; 
import { useEffect, useState } from "react";
import { logout } from "../../api/authApi";
import "./Navbar.css";

export default function Navbar() {
  const navigate = useNavigate();
  const location = useLocation(); 
  const [token, setToken] = useState(null);
  const [fullName, setFullName] = useState("");

  useEffect(() => {
    setToken(localStorage.getItem("token"));
    setFullName(localStorage.getItem("fullName") || "");
  }, [location]); 

  const handleLogout = () => {
    logout();
    setToken(null);
    setFullName("");
    navigate("/");
  };

  return (
    <div className="navbar">
      <div className="logo" onClick={() => navigate("/main")}>
        📚 ספריית השכונה
      </div>

      <div className="nav-actions">
        {token ? (
          <>
            <span className="user">
              👤 מחובר: {fullName}
            </span>

            <button className="logout-btn" onClick={handleLogout}>
              התנתקות
            </button>
          </>
        ) : (
          <>
            <button onClick={() => navigate("/")}>
              התחברות
            </button>

            <button onClick={() => navigate("/register")}>
              הרשמה
            </button>
          </>
        )}
      </div>
    </div>
  );
}