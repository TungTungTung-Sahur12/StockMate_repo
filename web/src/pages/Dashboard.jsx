import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/axios";
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

  const [totalProducts, setTotalProducts] = useState("—");
  const [lowStockCount, setLowStockCount] = useState("—");
  const [todaysSales, setTodaysSales] = useState("—");
  const [staffCount, setStaffCount] = useState("—");

  const isAdmin = auth?.role === "ADMIN";

  const refreshDashboardData = () => {
    api.get("/products").then((res) => {
      const products = res.data || [];
      setTotalProducts(products.length);
      setLowStockCount(products.filter((p) => p.isLowStock).length);
    }).catch(() => {});

    const today = new Date();
    const startDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}`;
    const endDate = startDate;

    api.get("/sales/summary", { params: { startDate, endDate } }).then((res) => {
      setTodaysSales(res.data?.totalCount ?? 0);
    }).catch(() => {});

    if (isAdmin) {
      api.get("/admin/users").then((res) => {
        const users = res.data || [];
        setStaffCount(users.filter((u) => u.role === "STAFF").length);
      }).catch(() => {});
    }
  };

  useEffect(() => {
    refreshDashboardData();

    const handleFocus = () => refreshDashboardData();
    window.addEventListener("focus", handleFocus);

    return () => window.removeEventListener("focus", handleFocus);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAdmin]);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

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
          <div className="stat-value">{totalProducts}</div>
          <div className="stat-label">Total Products</div>
        </div>
        <div className="stat-card float-2">
          <div className="stat-value">{lowStockCount}</div>
          <div className="stat-label">Low Stock Alerts</div>
        </div>
        <div className="stat-card float-3">
          <div className="stat-value">{todaysSales}</div>
          <div className="stat-label">Today's Sales</div>
        </div>
        {isAdmin && (
          <div className="stat-card float-1">
            <div className="stat-value">{staffCount}</div>
            <div className="stat-label">Staff Accounts</div>
          </div>
        )}
      </div>

      <div className="store-card shelf-card">
        <div className="inventory-toolbar">
          <h2>Featured Pieces</h2>
          <Link to="/inventory" className="btn-store">Open Inventory →</Link>
        </div>
        <p className="subtitle">
          Inventory summary prompt: "Track stock levels, surface low-stock essentials, and keep the ASCENDIA catalog ready for the next drop."
        </p>
        <div className="product-grid">
          {PRODUCTS.map((p, i) => (
            <div className="product-tile" key={p.name} style={{ animationDelay: `${i * 0.05}s` }}>
              <span className="product-name">{p.name}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="store-card admin-card">
        <div className="admin-card-text">
          <h2>Record a Sale</h2>
          <p className="subtitle">Log a sale and automatically update stock levels.</p>
        </div>
        <Link to="/record-sale" className="btn-store">
          Record a Sale →
        </Link>
      </div>

      {isAdmin && (
        <div className="store-card admin-card">
          <div className="admin-card-text">
            <h2>Staff Management</h2>
            <p className="subtitle">Manage staff access and keep the ASCENDIA team aligned.</p>
          </div>
          <Link to="/manage-users" className="btn-store">
            Manage Staff Accounts →
          </Link>
        </div>
      )}
    </div>
  );
}
