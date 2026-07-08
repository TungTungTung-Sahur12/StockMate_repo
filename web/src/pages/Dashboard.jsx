import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";
import "../styles/dashboard.css";

const PRODUCTS = [
  { name: "Tee" },
  { name: "Box Fit Tee" },
  { name: "Hoodie" },
  { name: "Pants" },
  { name: "Cap" },
  { name: "Polo" },
  { name: "Shorts" },
  { name: "Longsleeves" },
  { name: "Accessories" },
];

export default function Dashboard() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const isAdmin = auth?.role === "ADMIN";
  const hour = new Date().getHours();
  const greeting = hour < 12 ? "Good morning" : hour < 18 ? "Good afternoon" : "Good evening";

  return (
    <div className="store-page">
      <div className="store-header">
        <h1>ASCENDIA</h1>
        <div className="header-right">
          <span className="welcome-text">{greeting}, {auth?.name}!</span>
          <span className={`badge badge-${auth?.role?.toLowerCase()}`}>{auth?.role}</span>
          <button className="btn-small" onClick={handleLogout}>Log Out</button>
        </div>
      </div>

      <div className="signage">
        <span className="signage-title">ASCENDIA</span>
        <span className="signage-sub">Clothing brand inventory studio</span>
      </div>

      <div className="stat-row">
        <div className="stat-card float-1">
          <div className="stat-value">—</div>
          <div className="stat-label">Total Products</div>
        </div>
        <div className="stat-card float-2">
          <div className="stat-value">—</div>
          <div className="stat-label">Low Stock Alerts</div>
        </div>
        <div className="stat-card float-3">
          <div className="stat-value">—</div>
          <div className="stat-label">Today's Sales</div>
        </div>
        {isAdmin && (
          <div className="stat-card float-1">
            <div className="stat-value">—</div>
            <div className="stat-label">Staff Accounts</div>
          </div>
        )}
      </div>

      <div className="store-card shelf-card">
        <h2>Featured Pieces</h2>
        <p className="subtitle">
          Inventory tools will appear here as your ASCENDIA catalog grows.
        </p>
        <div className="product-grid">
          {PRODUCTS.map((p, i) => (
            <div className="product-tile" key={p.name} style={{ animationDelay: `${i * 0.05}s` }}>
              <span className="product-name">{p.name}</span>
            </div>
          ))}
        </div>
      </div>

      {isAdmin && (
        <div className="store-card admin-card">
          <div className="admin-card-text">
            <h2>ASCENDIA Team Accounts</h2>
            <p className="subtitle">Add staff accounts and manage who can access ASCENDIA.</p>
          </div>
          <Link to="/manage-users" className="btn-store">
            Manage Team Accounts →
          </Link>
        </div>
      )}

      {!isAdmin && (
        <div className="store-card staff-card">
          <h2>Staff Access</h2>
          <p className="subtitle">
            You're logged in as Staff. Inventory and sales tools will appear here once enabled by your Admin.
          </p>
        </div>
      )}
    </div>
  );
}