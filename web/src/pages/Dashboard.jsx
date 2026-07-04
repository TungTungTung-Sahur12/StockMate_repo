import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";
import "../styles/dashboard.css";

const PRODUCTS = [
  { emoji: "🧴", name: "Shampoo Sachets" },
  { emoji: "🥫", name: "Canned Goods" },
  { emoji: "🍬", name: "Candies" },
  { emoji: "🧃", name: "Softdrinks" },
  { emoji: "🍚", name: "Rice (Bigas)" },
  { emoji: "🧼", name: "Soap" },
  { emoji: "🥤", name: "Instant Coffee" },
  { emoji: "🍜", name: "Instant Noodles" },
  { emoji: "📶", name: "E-load" },
  { emoji: "🥖", name: "Bread (Pandesal)" },
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
  const greeting = hour < 12 ? "Magandang umaga" : hour < 18 ? "Magandang hapon" : "Magandang gabi";

  return (
    <div className="store-page">
      <div className="store-header">
        <h1>🏪 StockMate</h1>
        <div className="header-right">
          <span className="welcome-text">{greeting}, {auth?.name}!</span>
          <span className={`badge badge-${auth?.role?.toLowerCase()}`}>{auth?.role}</span>
          <button className="btn-small" onClick={handleLogout}>Log Out</button>
        </div>
      </div>

      <div className="signage">
        <div className="signage-inner">
          <span className="signage-title">Kuya Store</span>
          <span className="signage-sub">Sari-Sari Store Inventory System</span>
        </div>
      </div>

      <div className="stat-row">
        <div className="stat-card float-1">
          <span className="stat-icon">📦</span>
          <div>
            <div className="stat-value">—</div>
            <div className="stat-label">Total Products</div>
          </div>
        </div>
        <div className="stat-card float-2">
          <span className="stat-icon">⚠️</span>
          <div>
            <div className="stat-value">—</div>
            <div className="stat-label">Low Stock Alerts</div>
          </div>
        </div>
        <div className="stat-card float-3">
          <span className="stat-icon">💰</span>
          <div>
            <div className="stat-value">—</div>
            <div className="stat-label">Today's Sales</div>
          </div>
        </div>
        {isAdmin && (
          <div className="stat-card float-1">
            <span className="stat-icon">👥</span>
            <div>
              <div className="stat-value">—</div>
              <div className="stat-label">Staff Accounts</div>
            </div>
          </div>
        )}
      </div>

      <div className="store-card shelf-card">
        <h2>🧾 Coming to Your Shelf</h2>
        <p className="subtitle">
          Inventory features come in a later activity. Here's a peek at what your store carries.
        </p>
        <div className="product-grid">
          {PRODUCTS.map((p, i) => (
            <div className="product-tile" key={p.name} style={{ animationDelay: `${i * 0.05}s` }}>
              <span className="product-emoji">{p.emoji}</span>
              <span className="product-name">{p.name}</span>
            </div>
          ))}
        </div>
      </div>

      {isAdmin && (
        <div className="store-card admin-card">
          <div className="admin-card-text">
            <h2>👥 Store Accounts</h2>
            <p className="subtitle">Add Staff accounts and manage who can access your store's system.</p>
          </div>
          <Link to="/manage-users" className="btn-store">
            Manage User Accounts →
          </Link>
        </div>
      )}

      {!isAdmin && (
        <div className="store-card staff-card">
          <h2>🙋 Staff Access</h2>
          <p className="subtitle">
            You're logged in as Staff. Inventory and sales tools will appear here once enabled by your Admin.
          </p>
        </div>
      )}
    </div>
  );
}