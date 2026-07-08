import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "../styles/dashboard.css";

const CATEGORY_OPTIONS = [
  { value: "", label: "All Categories" },
  { value: "TEE", label: "Tee" },
  { value: "BOX_FIT_TEE", label: "Box Fit Tee" },
  { value: "HOODIE", label: "Hoodie" },
  { value: "PANTS", label: "Pants" },
  { value: "CAP", label: "Cap" },
  { value: "POLO", label: "Polo" },
  { value: "SHORTS", label: "Shorts" },
  { value: "LONGSLEEVES", label: "Longsleeves" },
  { value: "ACCESSORIES", label: "Accessories" },
];

const CATEGORY_LABELS = Object.fromEntries(CATEGORY_OPTIONS.filter((c) => c.value).map((c) => [c.value, c.label]));

const emptyForm = {
  name: "",
  category: "TEE",
  size: "",
  price: "",
  quantity: "",
  lowStockThreshold: "",
};

export default function Inventory() {
  const { auth } = useAuth();
  const [products, setProducts] = useState([]);
  const [category, setCategory] = useState("");
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const [loadingProducts, setLoadingProducts] = useState(false);

  const fetchProducts = async (selectedCategory = "") => {
    setLoadingProducts(true);
    try {
      const res = await api.get("/products", {
        params: selectedCategory ? { category: selectedCategory } : {},
      });
      setProducts(res.data || []);
    } catch (err) {
      setApiError("Failed to load inventory.");
    } finally {
      setLoadingProducts(false);
    }
  };

  useEffect(() => {
    fetchProducts("");
  }, []);

  const validate = () => {
    const nextErrors = {};

    if (!form.name.trim()) nextErrors.name = "Name is required";
    if (!form.category) nextErrors.category = "Category is required";

    if (!form.price) nextErrors.price = "Price is required";
    else {
      const parsedPrice = Number(form.price);
      if (!Number.isFinite(parsedPrice) || parsedPrice <= 0) {
        nextErrors.price = "Price must be greater than zero";
      }
    }

    if (form.quantity === "" || form.quantity === null) nextErrors.quantity = "Quantity is required";
    else {
      const parsedQuantity = Number(form.quantity);
      if (!Number.isInteger(parsedQuantity) || parsedQuantity < 0) {
        nextErrors.quantity = "Quantity cannot be negative";
      }
    }

    if (form.lowStockThreshold !== "") {
      const parsedThreshold = Number(form.lowStockThreshold);
      if (!Number.isInteger(parsedThreshold) || parsedThreshold < 0) {
        nextErrors.lowStockThreshold = "Low stock threshold cannot be negative";
      }
    }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError("");
    setSuccessMsg("");

    if (!validate()) return;

    setLoading(true);
    try {
      await api.post("/products", {
        name: form.name.trim(),
        category: form.category,
        size: form.size.trim() || null,
        price: Number(form.price),
        quantity: Number(form.quantity),
        lowStockThreshold: form.lowStockThreshold === "" ? 5 : Number(form.lowStockThreshold),
      });

      setSuccessMsg("Product added successfully.");
      setForm(emptyForm);
      fetchProducts(category);
    } catch (err) {
      setApiError(err.response?.data?.message || "Failed to add product.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (product) => {
    setApiError("");
    setSuccessMsg("");

    try {
      await api.delete(`/admin/products/${product.productId}`);
      setSuccessMsg("Product deleted successfully.");
      fetchProducts(category);
    } catch (err) {
      if (err.response?.status === 403) {
        setApiError("Only admins can delete products.");
      } else {
        setApiError(err.response?.data?.message || "Failed to delete product.");
      }
    }
  };

  const handleCategoryChange = (value) => {
    setCategory(value);
    fetchProducts(value);
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
        <span className="signage-title">Inventory</span>
        <span className="signage-sub">Manage the ASCENDIA catalog with add and delete actions.</span>
      </div>

      <div className="store-card">
        <h2>Add Product</h2>
        <p className="subtitle">Create a new SKU for the ASCENDIA collection using the same catalog rules as the backend.</p>

        {apiError && <div className="alert-error">{apiError}</div>}
        {successMsg && <div className="alert-success">{successMsg}</div>}

        <form onSubmit={handleSubmit} noValidate className="inventory-form">
          <div className="form-group">
            <label>Product Name</label>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
            {errors.name && <div className="field-error">{errors.name}</div>}
          </div>

          <div className="form-group">
            <label>Category</label>
            <select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
              {CATEGORY_OPTIONS.filter((c) => c.value).map((c) => (
                <option key={c.value} value={c.value}>{c.label}</option>
              ))}
            </select>
            {errors.category && <div className="field-error">{errors.category}</div>}
          </div>

          <div className="form-group">
            <label>Size (optional)</label>
            <input value={form.size} onChange={(e) => setForm({ ...form, size: e.target.value })} />
          </div>

          <div className="form-group">
            <label>Price</label>
            <input type="number" step="0.01" min="0.01" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} />
            {errors.price && <div className="field-error">{errors.price}</div>}
          </div>

          <div className="form-group">
            <label>Quantity</label>
            <input type="number" min="0" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} />
            {errors.quantity && <div className="field-error">{errors.quantity}</div>}
          </div>

          <div className="form-group">
            <label>Low Stock Threshold (optional)</label>
            <input type="number" min="0" value={form.lowStockThreshold} onChange={(e) => setForm({ ...form, lowStockThreshold: e.target.value })} />
            {errors.lowStockThreshold && <div className="field-error">{errors.lowStockThreshold}</div>}
          </div>

          <button className="btn-store" type="submit" disabled={loading}>
            {loading ? "Adding..." : "Add Product"}
          </button>
        </form>
      </div>

      <div className="store-card">
        <div className="inventory-toolbar">
          <h2>ASCENDIA Inventory</h2>
          <select value={category} onChange={(e) => handleCategoryChange(e.target.value)}>
            {CATEGORY_OPTIONS.map((c) => (
              <option key={c.value || "all"} value={c.value}>{c.label}</option>
            ))}
          </select>
        </div>

        {loadingProducts ? (
          <p className="subtitle">Loading inventory...</p>
        ) : products.length === 0 ? (
          <p className="subtitle">No products found for the selected category.</p>
        ) : (
          <div className="product-grid inventory-grid">
            {products.map((product) => (
              <div className="inventory-card" key={product.productId}>
                <div className="inventory-image">
                  {/* Real image placeholder; swap in an <img> once product photos are available. */}
                  <span>{CATEGORY_LABELS[product.category] ? CATEGORY_LABELS[product.category].toUpperCase() : "PRODUCT"}</span>
                </div>

                <div className="inventory-info">
                  <div className="inventory-title-row">
                    <h3>{product.name}</h3>
                    {auth?.role === "ADMIN" && (
                      <button className="btn-small danger" onClick={() => handleDelete(product)}>
                        Delete
                      </button>
                    )}
                  </div>
                  <p className="inventory-meta">Category: {CATEGORY_LABELS[product.category] || product.category}</p>
                  <p className="inventory-meta">Price: ₱{Number(product.price).toFixed(2)}</p>
                  <p className="inventory-meta">Quantity: {product.quantity}</p>
                  {product.isLowStock && <span className="badge badge-inactive">Low Stock</span>}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
