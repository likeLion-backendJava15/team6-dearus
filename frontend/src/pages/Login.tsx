import React, { useState } from "react";
import { Mail, Lock } from "lucide-react";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { login } from "../lib/api";
import { useNavigate } from "react-router-dom"; // ✅ 리다이렉트

export default function Login() {
  const navigate = useNavigate();
  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await login(userId, password);
      console.log("로그인 성공:", res.data);

      // ✅ JWT 저장
      localStorage.setItem("token", res.data.token);

      // ✅ 메인 페이지 이동
      navigate("/");
    } catch (err) {
      console.error(err);
      alert("로그인 실패!");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-100">
      <div className="bg-white/80 backdrop-blur-sm border border-purple-100 rounded-2xl shadow-lg p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">Welcome Back!</h1>
        <p className="text-gray-600 mb-6">아이디로 로그인해 주세요</p>

        <form onSubmit={handleLogin} className="space-y-4">
          <div className="relative">
            <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <Input
              type="text"
              placeholder="아이디"
              required
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              className="pl-10 py-3"
            />
          </div>

          <div className="relative">
            <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <Input
              type="password"
              placeholder="비밀번호"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="pl-10 py-3"
            />
          </div>

          <Button
            type="submit"
            className="w-full bg-gradient-to-r from-purple-500 to-blue-500 hover:from-purple-600 hover:to-blue-600 text-white py-3 rounded-full shadow-lg hover:shadow-xl transition-all duration-300"
          >
            로그인
          </Button>
        </form>

        <p className="text-sm text-gray-500 text-center mt-4">
          계정이 없으신가요?{" "}
          <a href="/signup" className="text-purple-600 hover:underline">회원가입</a>
        </p>
      </div>
    </div>
  );
}