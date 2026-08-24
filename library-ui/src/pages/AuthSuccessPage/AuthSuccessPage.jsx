import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { setUserData } from "../../api/authApi"; // 🌟 ייבוא פונקציית השמירה המרוכזת

export default function AuthSuccessPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const token = params.get("token");
    const fullName = params.get("fullName");
    const id = params.get("id");
    const email = params.get("email");

    if (token) {
      setUserData({
        token,
        fullName,
        id,
        email
      });
      
      navigate("/main");
    } else {
      navigate("/");
    }
  }, [params, navigate]);

  return (
    <div style={{ display: "flex", justifyContent: "center", alignItems: "center", height: "100vh", fontSize: "1.2rem" }}>
      🔄 מתחבר באמצעות Google, אנא המתן...
    </div>
  );
}