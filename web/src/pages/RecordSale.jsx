import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import "../styles/dashboard.css";

export default function RecordSale() {
  const [products, setProducts] = useState([]);
  const [sales, setSales] = useState([]);
  const [productId, setProductId] = useState("");
  const [quantity, setQuantity] = useState("");
  const [apiError, setApiError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const fetchProducts = async () => {
    try {
      const res = await api.get("/products");
      setProducts(res.data || []);
    } catch (err) {
      setApiError("Failed to load products.");
    }
  };

  const fetchSales = async () => {
    try {
      const res = await api.get("/sales");
      setSales(res.data || []);
    } catch (err) {
      setApiError("Failed to load sales history.");
    }
  };

  useEffect(() => {
    fetchProducts();
    fetchSales();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError("");
    setSuccessMsg("");

    if (!productId) {
      setApiError("Please select a product.");
      return;
    }
    const qty = Number(quantity);
    if (!Number.isInteger(qty) || qty < 1) {
      setApiError("Quantity must be at least 1.");
      return;
    }

    setLoading(true);
    try {
      const res = await api.post("/sales", {
        productId: Number(productId),
        quantitySold: qty,
      });
      setSuccessMsg(
        `Sale recorded: ${res.data.quantitySold}x ${res.data.productName} — ₱${Number(res.data.totalAmount).toFixed(2)}`
      );
      setProductId("");
      setQuantity("");
      fetchProducts();
      fetchSales();
    } catch (err) {
      setApiError(err.response?.data?.message || "Failed to record sale.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="store-page">
      <div className="store-header">
        <h1>ASCENDIA</h1>
        <Link to="/dashboard" className="btn-small" style={{ textDecoration: "none" }}>
          ← Back to Dashboard
        </Link>
      </div>

      <div className="signage">
        <span className="signage-title">Record a Sale</span>
        <span className="signage-sub">Select a product and quantity to log a sale.</span>
      </div>

      <div className="store-card">
        {apiError && <div className="alert-error">{apiError}</div>}
        {successMsg && <div className="alert-success">{successMsg}</div>}

        <form onSubmit={handleSubmit} noValidate className="inventory-form">
          <div className="form-group">
            <label>Product</label>
            <select value={productId} onChange={(e) => setProductId(e.target.value)}>
              <option value="">Select a product</option>
              {products.map((p) => (
                <option key={p.productId} value={p.productId}>
                  {p.name} — {p.quantity} in stock
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Quantity</label>
            <input
              type="number"
              min="1"
              value={quantity}
              onChange={(e) => setQuantity(e.target.value)}
            />
          </div>

          <button className="btn-store" type="submit" disabled={loading}>
            {loading ? "Recording..." : "Record Sale"}
          </button>
        </form>
      </div>

      <div className="store-card">
        <h2>Recent Sales</h2>
        {sales.length === 0 ? (
          <p className="subtitle">No sales recorded yet.</p>
        ) : (
          <table className="store-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>Qty</th>
                <th>Total</th>
                <th>Recorded By</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {sales.map((s) => (
                <tr key={s.saleId}>
                  <td>{s.productName}</td>
                  <td>{s.quantitySold}</td>
                  <td>₱{Number(s.totalAmount).toFixed(2)}</td>
                  <td>{s.recordedByName}</td>
                  <td>{new Date(s.createdAt).toLocaleString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}