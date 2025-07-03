import React, { useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import { getMe } from "./lib/api"; // 네 api.ts에 있는 거

export default function ProtectedRoute({ children }: { children: JSX.Element }) {
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        await getMe(); // 👉 로그인 상태라면 OK
        setIsAuthenticated(true);
      } catch (err) {
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };
    checkAuth();
  }, []);

  if (loading) {
    return <div className="text-center p-10">인증 확인 중...</div>;
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />;
}