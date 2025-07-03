import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Plus, Search, Calendar, BookOpen, LogOut, User, Users,
  BellDotIcon
} from 'lucide-react';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Card, CardContent, CardHeader } from '../components/ui/card';
import {
  getMe,
  getMyDiaries,
  createDiary,
  deleteDiary,
  getDiaryMembers,
  inviteMember,
  removeMember,
  acceptInvite,
  getMyInvites
} from '../lib/api';

export default function Home() {
  const [diaries, setDiaries] = useState<any[]>([]);
  const [userName, setUserName] = useState('');
  const [userEmail, setUserEmail] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [newDiaryName, setNewDiaryName] = useState('');

  const [isMemberModalOpen, setIsMemberModalOpen] = useState(false);
  const [selectedDiaryId, setSelectedDiaryId] = useState<number | null>(null);
  const [members, setMembers] = useState<any[]>([]);
  const [inviteEmail, setInviteEmail] = useState('');
  const [pendingInvites, setPendingInvites] = useState<any[]>([]);
  const [isInviteOpen, setIsInviteOpen] = useState(false);

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

  const handleCreateDiary = async () => {
    if (!newDiaryName.trim()) {
      alert('일기장명을 입력해주세요.');
      return;
    }
    try {
      await createDiary(newDiaryName);
      setIsCreateModalOpen(false);
      setNewDiaryName('');
      const res = await getMyDiaries();
      setDiaries(res.data);
    } catch (err) {
      console.error(err);
      alert('일기장 생성 실패!');
    }
  };

  const handleDeleteDiary = async (diaryId: number) => {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    try {
      await deleteDiary(diaryId);
      const diariesRes = await getMyDiaries();
      setDiaries(diariesRes.data);
    } catch (err) {
      console.error(err);
      alert('삭제 중 오류가 발생했습니다.');
    }
  };

  const openMemberModal = async (diaryId: number) => {
    try {
      setSelectedDiaryId(diaryId);
      const res = await getDiaryMembers(diaryId);
      setMembers(res.data);
      setIsMemberModalOpen(true);
    } catch (err) {
      console.error(err);
      alert('멤버 불러오기 실패!');
    }
  };

  const handleInviteMember = async () => {
    if (!inviteEmail.trim()) {
      alert('이메일을 입력해주세요.');
      return;
    }
    try {
      await inviteMember(selectedDiaryId!, inviteEmail);
      const res = await getDiaryMembers(selectedDiaryId!);
      setMembers(res.data);
      setInviteEmail('');
    } catch (err) {
      console.error(err);
      alert('초대 실패!');
    }
  };

  const handleRemoveMember = async (memberId: number) => {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    try {
      await removeMember(selectedDiaryId!, memberId);
      const res = await getDiaryMembers(selectedDiaryId!);
      setMembers(res.data);
    } catch (err) {
      console.error(err);
      alert('삭제 실패!');
    }
  };

  useEffect(() => {
    const fetchInvites = async () => {
      const res = await getMyInvites();
      setPendingInvites(res.data);
    };
    fetchInvites();
  }, []);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        setIsCreateModalOpen(false);
        setIsMemberModalOpen(false);
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  const handleAcceptInvite = async (diaryId: number) => {
    console.log('수락할 diaryId 직접 사용:', diaryId);
    await acceptInvite(diaryId);
    const res = await getMyInvites();
    setPendingInvites(res.data);
    // ✅ 수락 후 일기장 목록 다시 가져오기
    const diariesRes = await getMyDiaries();
    setDiaries(diariesRes.data);
  };

  if (!userName || !userEmail) {
    return <div className="text-center py-20">로딩 중...</div>;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-100">
      {/* ✅ 헤더 */}
      <div className="bg-white/80 backdrop-blur-sm border-b border-purple-100 sticky top-0 z-10">
        <div className="max-w-6xl mx-auto px-4 py-6">
          <div className="flex items-start justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-gradient-to-r from-purple-500 to-blue-500 rounded-full text-white">
                <BookOpen size={24} />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-gray-800">Dear Us</h1>
                <p className="text-gray-600">소중한 하루하루를 기록해보세요</p>
              </div>
            </div>

            {/* ✅ 사용자 + 로그아웃 + 새 일기장 버튼을 세로로 */}
            <div className="flex items-center justify-between gap-4 w-full sm:w-auto">

              {/* ✅ 사용자 정보 */}
              <div className="flex items-center gap-4">
                {/* ✅ 사용자 프로필 + 초대 알림 */}
                <div className="flex items-center gap-3 bg-white/60 backdrop-blur-sm rounded-full px-4 py-2 border border-purple-100 relative group">
                  <div className="w-10 h-10 rounded-full bg-gradient-to-br from-purple-400 to-pink-400 flex items-center justify-center overflow-hidden">
                    <User size={20} className="text-white" />
                  </div>
                  <div className="hidden sm:block">
                    <p className="text-sm font-medium text-gray-800">{userName}</p>
                    <p className="text-xs text-gray-500">{userEmail}</p>
                  </div>
                  {/* ✅ 초대 알림 */}
                  <Button onClick={() => setIsInviteOpen(!isInviteOpen)} variant="ghost" size="icon" className="p-1">
                    <BellDotIcon size={18} />
                    {pendingInvites.length > 0 && (
                      <span className="absolute top-0 right-0 inline-block w-2 h-2 bg-red-500 rounded-full"></span>
                    )}
                  </Button>
                  {isInviteOpen && (
                    <div className="absolute right-0 top-full mt-2 bg-white border border-gray-200 shadow-lg rounded p-4 w-64">
                      <p className="text-sm text-gray-600">초대 알림 목록</p>
                      <ul className="mt-2 space-y-1 text-xs text-gray-700">
                        {pendingInvites.map(invite => (
                          <li key={invite.id} className="flex items-center justify-between">
                            <span>{invite.diaryName} 초대</span>
                            <Button size="sm" className="text-xs px-2 py-1" onClick={() => handleAcceptInvite(invite.diaryId)}>수락</Button>
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}

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
                </div>
              </div>
            </div>
          </div>

          <div className="relative max-w-md mb-6">
            <Search size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
            <Input
              type="text"
              placeholder="일기장 검색..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 pr-4 py-3 rounded-full border-purple-200 focus:border-purple-400 focus:ring-purple-200"
            />
          </div>
          <div className="mt-4 flex justify-end">
            <Button
              className="bg-gradient-to-r from-purple-500 to-blue-500 hover:from-purple-600 hover:to-blue-600 text-white px-6 py-3 rounded-full shadow-lg hover:shadow-xl transition-all duration-300"
              onClick={() => setIsCreateModalOpen(true)}
            >
              <Plus size={20} className="mr-2" />
              새 일기장 만들기
            </Button>
          </div>
        </div>
      </div>

      {/* ✅ 새 일기장 모달 */}
      {isCreateModalOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
          <div className="bg-white p-6 rounded-lg shadow-md w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">새 일기장 만들기</h2>
            <Input
              placeholder="일기장명을 입력하세요"
              value={newDiaryName}
              onChange={(e) => setNewDiaryName(e.target.value)}
            />
            <div className="flex justify-end gap-2 mt-4">
              <Button onClick={() => setIsCreateModalOpen(false)} variant="ghost">
                취소
              </Button>
              <Button onClick={handleCreateDiary}>생성</Button>
            </div>
          </div>
        </div>
      )}

      {/* ✅ 멤버 관리 모달 */}
      {isMemberModalOpen && (
        <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
          <div className="bg-white p-6 rounded-lg shadow-md w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">멤버 관리</h2>

            <div className="h-48 overflow-y-auto border border-gray-200 rounded p-4 mb-4">
              {members.map(member => (
                <div
                  key={member.id}
                  className="flex justify-between items-center mb-2"
                >
                  <span>{member.nickname} ({member.email})</span>
                  <Button
                    size="sm"
                    variant="ghost"
                    onClick={() => handleRemoveMember(member.id)}
                  >
                    삭제
                  </Button>
                </div>
              ))}
            </div>

            <div className="flex gap-2 mb-4">
              <Input
                placeholder="초대할 이메일"
                value={inviteEmail}
                onChange={(e) => setInviteEmail(e.target.value)}
              />
              <Button onClick={handleInviteMember}>초대</Button>
            </div>

            <div className="flex justify-end">
              <Button variant="ghost" onClick={() => setIsMemberModalOpen(false)}>닫기</Button>
            </div>
          </div>
        </div>
      )}

      {/* ✅ 메인 */}
      <div className="max-w-6xl mx-auto px-4 py-8">
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

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {diaries
            .filter(diary => diary.name.includes(searchTerm))
            .map(diary => (
              <Card
                key={diary.id}
                className="bg-white/80 backdrop-blur-sm border border-purple-100 shadow-md hover:shadow-xl hover:border-purple-400 hover:scale-105 transition-all duration-300"
              >
                <CardHeader className="p-4 cursor-pointer" onClick={() => navigate(`/diary/${diary.id}`, { state: { diaryName: diary.name } })}> 
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
                    onClick={() => openMemberModal(diary.id)}
                  >
                    멤버 관리
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    className="mt-2"
                    onClick={() => handleDeleteDiary(diary.id)}
                  >
                    일기장 삭제
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
