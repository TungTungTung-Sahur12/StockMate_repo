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
  const [search, setSearch] = useState("");
  const [form, setForm] = useState(emptyForm);
  const [editingProductId, setEditingProductId] = useState(null);
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const [loadingProducts, setLoadingProducts] = useState(false);

  const fetchProducts = async (selectedCategory = category, selectedSearch = search) => {
    setLoadingProducts(true);
    try {
      const params = {};
      if (selectedCategory) params.category = selectedCategory;
      if (selectedSearch) params.name = selectedSearch;
      const res = await api.get("/products", { params });
      setProducts(res.data || []);
    } catch (err) {
      setApiError("Failed to load inventory.");
    } finally {
      setLoadingProducts(false);
    }
  };

  useEffect(() => {
    fetchProducts("", "");
  }, []);

  // Debounced search-as-you-type
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchProducts(category, search);
    }, 300);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search]);

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

    const payload = {
      name: form.name.trim(),
      category: form.category,
      size: form.size.trim() || null,
      price: Number(form.price),
      quantity: Number(form.quantity),
      lowStockThreshold: form.lowStockThreshold === "" ? 5 : Number(form.lowStockThreshold),
    };

    setLoading(true);
    try {
      if (editingProductId) {
        await api.put(`/products/${editingProductId}`, payload);
        setSuccessMsg("Product updated successfully.");
      } else {
        await api.post("/products", payload);
        setSuccessMsg("Product added successfully.");
      }
      setForm(emptyForm);
      setEditingProductId(null);
      fetchProducts(category, search);
      window.dispatchEvent(new Event("stockmate_inventory_updated"));
    } catch (err) {
      setApiError(err.response?.data?.message || "Failed to save product.");
    } finally {
      setLoading(false);
    }
  };

  const handleEditClick = (product) => {
    setEditingProductId(product.productId);
    setForm({
      name: product.name,
      category: product.category,
      size: product.size || "",
      price: String(product.price),
      quantity: String(product.quantity),
      lowStockThreshold: String(product.lowStockThreshold),
    });
    setApiError("");
    setSuccessMsg("");
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleCancelEdit = () => {
    setEditingProductId(null);
    setForm(emptyForm);
    setErrors({});
  };

  const handleDelete = async (product) => {
    setApiError("");
    setSuccessMsg("");

    const confirmed = window.confirm(`Delete "${product.name}"? This cannot be undone.`);
    if (!confirmed) return;

    try {
      await api.delete(`/products/${product.productId}`);
      setSuccessMsg("Product deleted successfully.");
      fetchProducts(category, search);
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
    fetchProducts(value, search);
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
        <span className="signage-sub">Manage the ASCENDIA catalog with add, edit, and delete actions.</span>
      </div>

      {auth?.role === "ADMIN" && (
        <div className="store-card">
          <h2>{editingProductId ? "Edit Product" : "Add Product"}</h2>
          <p className="subtitle">
            {editingProductId
              ? "Update this product's details below."
              : "Create a new SKU for the ASCENDIA collection using the same catalog rules as the backend."}
          </p>

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

            <div style={{ display: "flex", gap: "10px" }}>
              <button className="btn-store" type="submit" disabled={loading}>
                {loading ? "Saving..." : editingProductId ? "Update Product" : "Add Product"}
              </button>
              {editingProductId && (
                <button type="button" className="btn-small" onClick={handleCancelEdit}>
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>
      )}

      <div className="store-card">
        <div className="inventory-toolbar">
          <h2>ASCENDIA Inventory</h2>
          <div style={{ display: "flex", gap: "10px" }}>
            <input
              type="text"
              placeholder="Search by name..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
            <select value={category} onChange={(e) => handleCategoryChange(e.target.value)}>
              {CATEGORY_OPTIONS.map((c) => (
                <option key={c.value || "all"} value={c.value}>{c.label}</option>
              ))}
            </select>
          </div>
        </div>

        {loadingProducts ? (
          <p className="subtitle">Loading inventory...</p>
        ) : products.length === 0 ? (
          <p className="subtitle">No products found.</p>
        ) : (
          <div className="product-grid inventory-grid">
            {products.map((product) => (
              <div className="inventory-card" key={product.productId}>
                <div className="inventory-image">
                  <span>{CATEGORY_LABELS[product.category] ? CATEGORY_LABELS[product.category].toUpperCase() : "PRODUCT"}</span>
                </div>

                <div className="inventory-info">
                  <div className="inventory-title-row">
                    <h3>{product.name}</h3>
                    {auth?.role === "ADMIN" && (
                      <div style={{ display: "flex", gap: "6px" }}>
                        <button className="btn-small" onClick={() => handleEditClick(product)}>
                          Edit
                        </button>
                        <button className="btn-small danger" onClick={() => handleDelete(product)}>
                          Delete
                        </button>
                      </div>
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
