import React, { useState } from "react";
import { Mail, Lock, User } from "lucide-react";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { signup } from "../lib/api"; // api.ts에 만든 함수

export default function Signup() {
  const [email, setEmail] = useState("");
  const [nickname, setNickname] = useState("");
  const [password, setPassword] = useState("");
  const [passwordConfirm, setPasswordConfirm] = useState("");

  const handleSignup = async (e: React.FormEvent) => {
    e.preventDefault();
    if (password !== passwordConfirm) {
      alert("비밀번호가 일치하지 않습니다!");
      return;
    }

    try {
      const res = await signup(email, password, nickname);
      console.log("회원가입 성공:", res.data);
      alert("회원가입 성공! 로그인해주세요.");
      window.location.href = "/#/login";
    } catch (err) {
      console.error(err);
      alert("회원가입 실패!");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-100">
      <div className="bg-white/80 backdrop-blur-sm border border-purple-100 rounded-2xl shadow-lg p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-gray-800 mb-2">Create Account</h1>
        <p className="text-gray-600 mb-6">정보를 입력해 주세요</p>
        <form onSubmit={handleSignup} className="space-y-4">
          <div className="relative">
            <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <Input
              type="email"
              placeholder="이메일"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="pl-10 py-3"
            />
          </div>

          <div className="relative">
            <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <Input
              type="text"
              placeholder="닉네임"
              required
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
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

          <div className="relative">
            <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
            <Input
              type="password"
              placeholder="비밀번호 확인"
              required
              value={passwordConfirm}
              onChange={(e) => setPasswordConfirm(e.target.value)}
              className="pl-10 py-3"
            />
          </div>

          <Button
            type="submit"
            className="w-full bg-gradient-to-r from-purple-500 to-blue-500 hover:from-purple-600 hover:to-blue-600 text-white py-3 rounded-full shadow-lg hover:shadow-xl transition-all duration-300"
          >
            회원가입
          </Button>
        </form>

        <p className="text-sm text-gray-500 text-center mt-4">
          이미 계정이 있으신가요?{" "}
          <a href="/#/login" className="text-purple-600 hover:underline">로그인</a>
        </p>
      </div>
    </div>
  );
}