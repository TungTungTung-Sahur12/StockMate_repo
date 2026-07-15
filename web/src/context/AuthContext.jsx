import { createContext, useContext, useState } from "react";
import api from "../api/axios";

const AuthContext = createContext(null);

const getStoredAuth = () => {
  try {
    const savedAuth = localStorage.getItem("stockmate_auth");
    if (!savedAuth) return null;

    const parsed = JSON.parse(savedAuth);
    if (parsed?.token) {
      api.defaults.headers.common["Authorization"] = `Bearer ${parsed.token}`;
      return parsed;
    }
  } catch (error) {
    console.error("Failed to restore saved auth", error);
  }

  localStorage.removeItem("stockmate_auth");
  return null;
};

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(getStoredAuth);

  const login = (data) => {
    setAuth(data);
    localStorage.setItem("stockmate_auth", JSON.stringify(data));
    api.defaults.headers.common["Authorization"] = `Bearer ${data.token}`;
  };

  const logout = () => {
    setAuth(null);
    localStorage.removeItem("stockmate_auth");
    delete api.defaults.headers.common["Authorization"];
  };

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);