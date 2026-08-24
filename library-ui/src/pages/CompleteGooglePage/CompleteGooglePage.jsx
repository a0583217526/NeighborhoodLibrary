import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { fetchNeighborhoods, completeGoogleRegistration } from "../../api/authApi"; 
import "./CompleteGooglePage.css"; 

export default function CompleteGooglePage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();

  const email = params.get("email");
  const fullName = params.get("fullName");

  const [phone, setPhone] = useState("");
  const [neighborhood, setNeighborhood] = useState("");
  const [neighborhoodsList, setNeighborhoodsList] = useState([]);

  useEffect(() => {
    const getNeighborhoods = async () => {
      try {
        const data = await fetchNeighborhoods();
        setNeighborhoodsList(data);
      } catch (err) {
        console.error("נכשלה טעינת השכונות", err);
      }
    };
    getNeighborhoods();
  }, []);

  const handleSubmit = async () => {
    try {
      await completeGoogleRegistration({
        email,
        fullName,
        phone,
        neighborhood, 
      });

      navigate("/main");
    } catch (err) {
      alert("שגיאה בהרשמה");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card onboarding-card">
        <h1 className="auth-title">השלמת הרשמה</h1>
        <p className="auth-subtitle">נשמח לכמה פרטים נוספים כדי לסיים</p>

        <div className="google-user-preview">
          <div className="preview-row">
            <span className="preview-label">שם:</span>
            <span className="preview-value">{fullName}</span>
          </div>
          <div className="preview-row">
            <span className="preview-label">אימייל:</span>
            <span className="preview-value">{email}</span>
          </div>
        </div>

        <input
          className="auth-input"
          placeholder="מספר טלפון"
          type="tel"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
        />

        <input
          className="auth-input"
          placeholder="בחר שכונה או הקלד חדשה..."
          value={neighborhood}
          list="google-neighborhoods-list"
          onChange={(e) => setNeighborhood(e.target.value)}
        />

        <datalist id="google-neighborhoods-list">
          {neighborhoodsList.map((n) => (
            <option key={n.id || n.name} value={n.name} />
          ))}
        </datalist>

        <button className="auth-button primary" onClick={handleSubmit}>
          סיום הרשמה ויוצאים לדרך
        </button>
      </div>
    </div>
  );
}