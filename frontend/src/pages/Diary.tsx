import React, { useEffect, useState } from 'react';
import { useParams, useLocation } from 'react-router-dom';
import { getEntries, createEntry } from '../lib/api';

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

    useEffect(() => {
        const fetchEntries = async () => {
            if (!id) return;
            const res = await getEntries(Number(id));
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

    return (
        <div className="min-h-screen bg-gradient-to-br from-purple-50 via-blue-50 to-indigo-100">
            <div className="max-w-6xl mx-auto px-4 py-6">
                <h1 className="text-3xl font-bold mb-6 text-gray-800">일기장 #{diaryName}</h1>

                <div className="flex items-center gap-4 mb-4">
                    <button onClick={() => setIsCreateModalOpen(true)} className="bg-purple-500 text-white px-4 py-2 rounded shadow hover:shadow-lg">일기 작성</button>
                    <button className="bg-red-500 text-white px-4 py-2 rounded shadow hover:shadow-lg">일기 삭제</button>
                </div>

                <ul className="space-y-4">
                    {entries.map(entry => (
                        <li key={entry.id} className="flex items-center p-4 bg-white/80 backdrop-blur-sm rounded shadow cursor-pointer hover:shadow-md transition-all">
                            <input type="checkbox" className="mr-4" />
                            <div>
                                <h2 className="text-lg font-semibold text-gray-800">{entry.title}</h2>
                                <p className="text-gray-600">{entry.content}</p>
                            </div>
                        </li>
                    ))}
                </ul>

                {entries.length === 0 && <p className="text-gray-500">작성된 일기가 없습니다.</p>}

                <div className="mt-8 text-center">
                    <button className="px-4 py-2 border rounded mx-1">1</button>
                </div>
            </div>

            {/* 작성 모달 */}
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
    );
}
