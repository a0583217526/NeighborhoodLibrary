import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { register, loginWithGoogle, fetchNeighborhoods } from "../../api/authApi";
import "./RegisterPage.css";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [neighborhoods, setNeighborhoods] = useState([]); // שמירת השכונות מה-DB

  const [form, setForm] = useState({
    fullName: "",
    email: "",
    password: "",
    phone: "",
    neighborhoodName: "",
  });

  // משיכת השכונות בטעינת העמוד
  useEffect(() => {
    const getNeighborhoods = async () => {
      try {
        const data = await fetchNeighborhoods();
        setNeighborhoods(data);
      } catch (err) {
        console.error("נכשלה טעינת השכונות", err);
      }
    };
    getNeighborhoods();
  }, []);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleRegister = async () => {
    try {
      await register(form);
      navigate("/main");
    } catch (err) {
      alert("שגיאה בהרשמה");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1 className="auth-title">הרשמה</h1>

        <input name="fullName" className="auth-input" placeholder="שם מלא" onChange={handleChange} />
        <input name="email" className="auth-input" placeholder="אימייל" onChange={handleChange} />
        <input name="password" className="auth-input" type="password" placeholder="סיסמה" onChange={handleChange} />
        <input name="phone" className="auth-input" placeholder="טלפון" onChange={handleChange} />
        
        {/* 🌟 שימוש ב-list שמקושר ל-datalist למטה */}
        <input 
          name="neighborhoodName" 
          className="auth-input" 
          placeholder="בחר שכונה או הקלד חדשה..." 
          list="neighborhoods-list" 
          onChange={handleChange} 
        />
        
        {/* 🌟 רשימת האופציות הנסתרת שהדפדפן מציג כהשלמה אוטומטית */}
        <datalist id="neighborhoods-list">
          {neighborhoods.map((n) => (
            <option key={n.id || n.name} value={n.name} />
          ))}
        </datalist>

        <button className="auth-button primary" onClick={handleRegister}>
          הרשמה
        </button>

        <div className="divider">או</div>

        <button className="google-button" onClick={loginWithGoogle}>
          <img src="https://developers.google.com/identity/images/g-logo.png" alt="google" />
          הרשמה עם Google
        </button>

        <p className="switch-text">
          כבר יש לך חשבון? <span onClick={() => navigate("/")}>התחבר כאן</span>
        </p>
      </div>
    </div>
  );
}