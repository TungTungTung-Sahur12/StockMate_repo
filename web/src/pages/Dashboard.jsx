import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Dashboard() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div style={{ padding: 32, fontFamily: "Segoe UI, Arial, sans-serif" }}>
      <h1>StockMate Dashboard</h1>
      <p>Welcome, {auth?.name} ({auth?.role}).</p>
      <p style={{ color: "#6b7280" }}>Placeholder — inventory features come in a later activity.</p>
      <button onClick={handleLogout} style={{ marginTop: 16 }}>Log Out</button>
    </div>
  );
}