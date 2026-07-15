import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import "../styles/dashboard.css";

export default function ManageUsers() {
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({ name: "", email: "", password: "" });
  const [editingUserId, setEditingUserId] = useState(null);
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const fetchUsers = async () => {
    try {
      const res = await api.get("/admin/users");
      setUsers(res.data);
    } catch (err) {
      setApiError("Failed to load users.");
    }
  };

  useEffect(() => { fetchUsers(); }, []);

  const validate = (isEdit = false) => {
    const e = {};
    if (!form.name.trim()) e.name = "Name is required";
    if (!form.email.trim()) e.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) e.email = "Invalid email format";
    if (!isEdit && !form.password) e.password = "Password is required";
    else if (!isEdit && form.password.length < 8) e.password = "Must be at least 8 characters";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError("");
    setSuccessMsg("");
    if (!validate(editingUserId)) return;

    setLoading(true);
    try {
      if (editingUserId) {
        await api.put(`/admin/users/${editingUserId}`, { name: form.name.trim(), email: form.email.trim() });
        setSuccessMsg(`Staff account updated successfully.`);
      } else {
        await api.post("/admin/staff", form);
        setSuccessMsg(`Staff account for ${form.name} created successfully.`);
      }
      setForm({ name: "", email: "", password: "" });
      setEditingUserId(null);
      fetchUsers();
    } catch (err) {
      setApiError(err.response?.data?.message || (editingUserId ? "Failed to update staff account." : "Failed to create staff account."));
    } finally {
      setLoading(false);
    }
  };

  const handleEditClick = (user) => {
    setEditingUserId(user.userId);
    setForm({ name: user.name, email: user.email, password: "" });
    setApiError("");
    setSuccessMsg("");
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleCancelEdit = () => {
    setEditingUserId(null);
    setForm({ name: "", email: "", password: "" });
    setErrors({});
  };

  const toggleStatus = async (user) => {
    try {
      await api.patch(`/admin/users/${user.userId}/status`, { isActive: !user.isActive });
      fetchUsers();
    } catch (err) {
      setApiError(err.response?.data?.message || "Failed to update status.");
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

      <div className="store-card">
        <h2>{editingUserId ? "Edit Staff Account" : "Add Staff Account"}</h2>
        <p className="subtitle">Only the ASCENDIA owner (Admin) can create or update staff access.</p>

        {apiError && <div className="alert-error">{apiError}</div>}
        {successMsg && <div className="alert-success">{successMsg}</div>}

        <form onSubmit={handleSubmit} noValidate className="staff-form">
          <div className="form-group">
            <label>Full Name</label>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
            {errors.name && <div className="field-error">{errors.name}</div>}
          </div>
          <div className="form-group">
            <label>Email</label>
            <input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
            {errors.email && <div className="field-error">{errors.email}</div>}
          </div>
          {!editingUserId && (
            <div className="form-group">
              <label>Temporary Password</label>
              <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
              {errors.password && <div className="field-error">{errors.password}</div>}
            </div>
          )}
          <div style={{ display: "flex", gap: "10px" }}>
            <button className="btn-store" type="submit" disabled={loading}>
              {loading ? (editingUserId ? "Updating..." : "Adding...") : editingUserId ? "Update Staff" : "Add Staff"}
            </button>
            {editingUserId && (
              <button type="button" className="btn-small" onClick={handleCancelEdit}>
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="store-card">
        <h2>ASCENDIA Team Accounts</h2>
        <table className="store-table">
          <thead>
            <tr><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th>Action</th></tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.userId}>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td><span className={`badge badge-${u.role.toLowerCase()}`}>{u.role}</span></td>
                <td><span className={`badge ${u.isActive ? "badge-active" : "badge-inactive"}`}>
                  {u.isActive ? "Active" : "Inactive"}
                </span></td>
                <td>
                  {u.role !== "ADMIN" && (
                    <div style={{ display: "flex", gap: "6px" }}>
                      <button className="btn-small" onClick={() => handleEditClick(u)}>
                        Edit
                      </button>
                      <button className="btn-small" onClick={() => toggleStatus(u)}>
                        {u.isActive ? "Deactivate" : "Activate"}
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}