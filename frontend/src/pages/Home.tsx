import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Plus, Search, Calendar, BookOpen, LogOut, User, Users
} from 'lucide-react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Card, CardContent, CardHeader } from '../components/ui/card';
import { getMe, getMyDiaries } from '../lib/api';

export default function Home() {
  const [diaries, setDiaries] = useState<any[]>([]);
  const [userName, setUserName] = useState('');
  const [userEmail, setUserEmail] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  const navigate = useNavigate();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await getMe();
        setUserName(res.data.nickname || res.data.userId);
        setUserEmail(res.data.email);
      } catch (err) {
        console.error(err);
      }
    };

    const fetchDiaries = async () => {
      try {
        const res = await getMyDiaries();
        setDiaries(res.data);
      } catch (err) {
        console.error(err);
      }
    };

    fetchUser();
    fetchDiaries();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-100">
      {/* ✅ ✅ ✅ 기존 헤더 그대로 유지! */}
      <div className="bg-white/80 backdrop-blur-sm border-b border-purple-100 sticky top-0 z-10">
        <div className="max-w-6xl mx-auto px-4 py-6">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-gradient-to-r from-purple-500 to-blue-500 rounded-full text-white">
                <BookOpen size={24} />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-gray-800">Dear Us</h1>
                <p className="text-gray-600">소중한 하루하루를 기록해보세요</p>
              </div>
            </div>

            <div className="flex items-center gap-4">
              {/* 사용자 프로필 */}
              <div className="flex items-center gap-3 bg-white/60 backdrop-blur-sm rounded-full px-4 py-2 border border-purple-100">
                <div className="w-10 h-10 rounded-full bg-gradient-to-br from-purple-400 to-pink-400 flex items-center justify-center overflow-hidden">
                  <User size={20} className="text-white" />
                </div>
                <div className="hidden sm:block">
                  <p className="text-sm font-medium text-gray-800">{userName}</p>
                  <p className="text-xs text-gray-500">{userEmail}</p>
                </div>
              </div>

              {/* 로그아웃 버튼 */}
              <Button
                variant="ghost"
                size="sm"
                className="text-gray-600 hover:text-gray-800 hover:bg-white/60 rounded-full p-2"
                onClick={() => {
                  if (confirm('로그아웃 하시겠습니까?')) {
                    localStorage.removeItem('token');
                    navigate('/login');
                  }
                }}
              >
                <LogOut size={18} />
              </Button>

              {/* 새 일기 작성 버튼 */}
              <Link to="/write">
                <Button className="bg-gradient-to-r from-purple-500 to-blue-500 hover:from-purple-600 hover:to-blue-600 text-white px-6 py-3 rounded-full shadow-lg hover:shadow-xl transition-all duration-300">
                  <Plus size={20} className="mr-2" />
                  새 일기 작성
                </Button>
              </Link>
            </div>
          </div>

          {/* 검색 바 */}
          <div className="relative max-w-md">
            <Search size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
            <Input
              type="text"
              placeholder="일기장 검색..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 pr-4 py-3 rounded-full border-purple-200 focus:border-purple-400 focus:ring-purple-200"
            />
          </div>
        </div>
      </div>

      {/* ✅ 메인 콘텐츠 */}
      <div className="max-w-6xl mx-auto px-4 py-8">
        {/* 통계 카드 */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <Card className="bg-white/70 backdrop-blur-sm border-purple-100 shadow-lg hover:shadow-xl transition-all duration-300">
            <CardContent className="p-6 text-center">
              <div className="p-3 bg-purple-100 rounded-full w-fit mx-auto mb-4">
                <BookOpen size={24} className="text-purple-600" />
              </div>
              <h3 className="text-2xl font-bold text-gray-800">{diaries.length}</h3>
              <p className="text-gray-600">총 일기장 수</p>
            </CardContent>
          </Card>

          <Card className="bg-white/70 backdrop-blur-sm border-purple-100 shadow-lg hover:shadow-xl transition-all duration-300">
            <CardContent className="p-6 text-center">
              <div className="p-3 bg-blue-100 rounded-full w-fit mx-auto mb-4">
                <Calendar size={24} className="text-blue-600" />
              </div>
              <h3 className="text-2xl font-bold text-gray-800">
                {new Date().toLocaleDateString('ko-KR')}
              </h3>
              <p className="text-gray-600">오늘</p>
            </CardContent>
          </Card>
        </div>

        {/* ✅ 일기장 목록 */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {diaries.map((diary) => (
            <Card
              key={diary.id}
              className="bg-white/80 backdrop-blur-sm border border-purple-100 shadow-md hover:shadow-lg transition-all duration-300"
            >
              <CardHeader className="p-4">
                <h3 className="text-lg font-bold text-gray-800">{diary.name}</h3>
              </CardHeader>
              <CardContent className="p-4 flex flex-col gap-2">
                <div className="flex items-center gap-2 text-gray-600 text-sm">
                  <Calendar size={16} />
                  <span>{new Date(diary.createdAt).toLocaleDateString('ko-KR')}</span>
                </div>
                <div className="flex items-center gap-2 text-gray-600 text-sm">
                  <Users size={16} />
                  <span>{diary.memberCount}명</span>
                </div>
                <Button
                  size="sm"
                  className="mt-2"
                  onClick={() => {
                    console.log(`멤버 관리 이동: ${diary.id}`);
                  }}
                >
                  멤버 관리
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>

        {diaries.length === 0 && (
          <div className="text-center py-16">
            <div className="p-4 bg-gray-100 rounded-full w-fit mx-auto mb-4">
              <Search size={32} className="text-gray-400" />
            </div>
            <h3 className="text-xl font-semibold text-gray-600 mb-2">
              일기장이 없습니다
            </h3>
            <p className="text-gray-500">새 일기장을 만들어보세요</p>
          </div>
        )}
      </div>
    </div>
  );
}