import React, { useEffect, useState } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import { getEntries, createEntry, deleteEntry } from '../lib/api';

const EMOTIONS = [
    { value: '행복해', label: '😀 행복해' },
    { value: '즐거워', label: '😊 즐거워' },
    { value: '감사해', label: '🙏 감사해' },
    { value: '사랑해', label: '❤️ 사랑해' },
    { value: '뿌듯해', label: '😌 뿌듯해' },
    { value: '그저그래', label: '😐 그저그래' },
    { value: '화나', label: '😡 화나' },
    { value: '힘들어', label: '😥 힘들어' },
];

export default function Diary() {
    const { id } = useParams<{ id: string }>();
    const location = useLocation();
    const [entries, setEntries] = useState<any[]>([]);
    const [diaryName, setDiaryName] = useState(location.state?.diaryName || '');
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [title, setTitle] = useState('');
    const [emotion, setEmotion] = useState(EMOTIONS[0].value);
    const [content, setContent] = useState('');
    const [imageUrl, setImageUrl] = useState('');
    const [tags, setTags] = useState('');
    const [selectedEntry, setSelectedEntry] = useState<any | null>(null);
    const [selectedEntries, setSelectedEntries] = useState<number[]>([]);
    

    useEffect(() => {
        const fetchEntries = async () => {
            if (!id) return;
            const res = await getEntries(Number(id));
            console.log('✅ entries:', res.data);
            setEntries(res.data);
        };
        fetchEntries();
    }, [id]);

    const handleCreateEntry = async () => {
        if (!id) return;
        if (!title.trim() || !content.trim()) {
            alert('제목과 내용을 모두 입력해주세요.');
            return;
        }
        try {
            const payload = {
                diaryId: Number(id),
                title: title.trim(),
                content: content.trim(),
                emotion: emotion,
                tags: tags ? tags.split(',').map(t => t.trim()) : [],
                imageUrl: imageUrl?.trim() || undefined
            };
            console.log('📦 payload', JSON.stringify(payload, null, 2));
            await createEntry(payload);
            setIsCreateModalOpen(false);
            setTitle('');
            setContent('');
            setImageUrl('');
            setTags('');
            const res = await getEntries(Number(id));
            setEntries(res.data);
        } catch (err) {
            console.error('❌ payload 상세:', err);
            alert('일기 생성에 실패했습니다.');
        }
    };
    // ✅ 선택된 일기 핸들러
    const handleSelectEntry = (entryId: number) => {
        setSelectedEntries(prev =>
            prev.includes(entryId)
                ? prev.filter(id => id !== entryId)
                : [...prev, entryId]
        );
    };
    // ✅ 삭제 핸들러
    const handleDeleteEntries = async () => {
        if (selectedEntries.length === 0) {
            alert('삭제할 일기를 선택해주세요.');
            return;
        }
        if (!confirm('정말 삭제하시겠습니까?')) return;

        try {
            for (const entryId of selectedEntries) {
                await deleteEntry(entryId);
            }
            setSelectedEntries([]);
            const res = await getEntries(Number(id));
            setEntries(res.data);
            alert('삭제 완료!');
        } catch (error) {
            console.error(error);
            alert('삭제 실패!');
        }
    };


    return (
        <div className="min-h-screen bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-100">
            <div className="max-w-6xl mx-auto px-6 py-10">
                <h1 className="text-4xl font-extrabold mb-8 text-purple-900">일기장 #{diaryName}</h1>

                <div className="flex items-center gap-4 mb-8">
                    <button type="button" onClick={() => setIsCreateModalOpen(true)} className="bg-gradient-to-r from-purple-500 to-blue-500 text-white px-5 py-3 rounded-full shadow-md hover:shadow-xl transition-all">일기 작성</button>
                    <button type="button" onClick={() => handleDeleteEntries()} className="bg-gradient-to-r from-pink-500 to-red-500 text-white px-5 py-3 rounded-full shadow-md hover:shadow-xl transition-all">일기 삭제</button>
                </div>

                <ul className="space-y-4">
                    {entries.map(entry => (
                        <li
                            key={entry.id}
                            className="flex items-center p-5 bg-white/90 backdrop-blur rounded-2xl border border-purple-200 shadow-md hover:shadow-xl transition"
                        >
                            <input
                                type="checkbox"
                                className="mr-4"
                                checked={selectedEntries.includes(entry.id)}
                                onChange={() => handleSelectEntry(entry.id)}
                                onClick={(e) => e.stopPropagation()}
                            />
                            <div className="cursor-pointer" onClick={() => setSelectedEntry(entry)}>
                                <h2 className="text-lg font-semibold text-purple-800">{entry.title}</h2>
                            </div>
                        </li>
                    ))}
                </ul>

                {selectedEntry && (
                    <div className="fixed inset-0 flex items-center justify-center bg-black/60 z-50">
                        <div className="bg-white/90 backdrop-blur p-8 rounded-3xl shadow-2xl border border-purple-200 w-full max-w-lg relative animate-fadeIn">
                            <h2 className="text-2xl font-bold mb-4 text-purple-700">{selectedEntry.title}</h2>

                            <p className="text-sm text-gray-500 mb-1">작성자: {selectedEntry.authorNickname || '알 수 없음'}</p>
                            <p className="text-sm text-gray-500 mb-4">작성일: {new Date(selectedEntry.createdAt).toLocaleDateString('ko-KR')}</p>
                            <p className="text-sm text-gray-500 mb-4">{selectedEntry.emotion === '행복해' ? '😀 행복해' : selectedEntry.emotion === '즐거워' ? '😊 즐거워' : selectedEntry.emotion === '감사해' ? '🙏 감사해' : selectedEntry.emotion === '사랑해' ? '❤️ 사랑해' : selectedEntry.emotion === '뿌듯해' ? '😌 뿌듯해' : selectedEntry.emotion === '그저그래' ? '😐 그저그래' : selectedEntry.emotion === '화나' ? '😡 화나' : selectedEntry.emotion === '힘들어' ? '😥 힘들어' : selectedEntry.emotion}</p>

                            <div className="bg-white/60 backdrop-blur-sm border border-purple-100 p-4 rounded-xl text-gray-800 mb-4 whitespace-pre-line shadow-inner">{selectedEntry.content}</div>
                            <p className="text-sm text-gray-500 mb-4">태그: {selectedEntry.tags.length > 0 ? selectedEntry.tags.join(', ') : '없음'}</p>
                            {selectedEntry.imageUrl && (
                                <img src={selectedEntry.imageUrl} alt="일기 이미지" className="mt-4 w-full rounded-2xl border border-purple-200 shadow-md" />
                            )}

                            <div className="flex justify-end mt-6">
                                <button
                                    onClick={() => setSelectedEntry(null)}
                                    className="px-6 py-2 bg-gradient-to-r from-purple-500 to-blue-500 text-white rounded-full shadow hover:shadow-lg transition"
                                >
                                    닫기
                                </button>
                            </div>
                        </div>
                    </div>
                )}


                {entries.length === 0 && <p className="text-gray-500">작성된 일기가 없습니다.</p>}

                <div className="mt-10 text-center">
                    <button type="button" className="px-4 py-2 border border-purple-300 rounded-full hover:bg-purple-50 transition">1</button>
                </div>

                {isCreateModalOpen && (
                    <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
                        <div className="bg-white p-6 rounded-lg shadow-md w-full max-w-md">
                            <h2 className="text-xl font-bold mb-4">새 일기 작성</h2>
                            <input value={title} onChange={e => setTitle(e.target.value)} placeholder="제목" className="w-full mb-2 p-2 border rounded" />
                            <select value={emotion} onChange={e => setEmotion(e.target.value)} className="w-full mb-2 p-2 border rounded">
                                {EMOTIONS.map(e => (
                                    <option key={e.value} value={e.value}>{e.label}</option>
                                ))}
                            </select>
                            <textarea value={content} onChange={e => setContent(e.target.value)} placeholder="내용" className="w-full mb-2 p-2 border rounded"></textarea>
                            <input value={tags} onChange={e => setTags(e.target.value)} placeholder="태그 (쉼표로 구분)" className="w-full mb-2 p-2 border rounded" />
                            <input value={imageUrl} onChange={e => setImageUrl(e.target.value)} placeholder="이미지 URL" className="w-full mb-4 p-2 border rounded" />
                            <div className="flex justify-end gap-2">
                                <button onClick={() => setIsCreateModalOpen(false)} className="px-4 py-2 bg-gray-200 rounded">취소</button>
                                <button onClick={handleCreateEntry} className="px-4 py-2 bg-purple-500 text-white rounded">생성</button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
