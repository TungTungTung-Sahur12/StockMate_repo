import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";
import "../styles/dashboard.css";

export default function ManageUsers() {
  const [users, setUsers] = useState([]);
  const [form, setForm] = useState({ name: "", email: "", password: "" });
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

  const validate = () => {
    const e = {};
    if (!form.name.trim()) e.name = "Name is required";
    if (!form.email.trim()) e.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) e.email = "Invalid email format";
    if (!form.password) e.password = "Password is required";
    else if (form.password.length < 8) e.password = "Must be at least 8 characters";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setApiError("");
    setSuccessMsg("");
    if (!validate()) return;

    setLoading(true);
    try {
      await api.post("/admin/staff", form);
      setSuccessMsg(`Staff account for ${form.name} created successfully.`);
      setForm({ name: "", email: "", password: "" });
      fetchUsers();
    } catch (err) {
      setApiError(err.response?.data?.message || "Failed to create staff account.");
    } finally {
      setLoading(false);
    }
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
        <h2>Add Staff Account</h2>
        <p className="subtitle">Only the ASCENDIA owner (Admin) can create staff access.</p>

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
          <div className="form-group">
            <label>Temporary Password</label>
            <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
            {errors.password && <div className="field-error">{errors.password}</div>}
          </div>
          <button className="btn-store" type="submit" disabled={loading}>
            {loading ? "Adding..." : "Add Staff"}
          </button>
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
                    <button className="btn-small" onClick={() => toggleStatus(u)}>
                      {u.isActive ? "Deactivate" : "Activate"}
                    </button>
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