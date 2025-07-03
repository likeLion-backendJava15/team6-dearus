import axios from "axios";

// Axios 인스턴스 생성 (필요하면 JWT 헤더 붙이기)
const api = axios.create({
  baseURL: "/api",
  withCredentials: true, // 필요 시 쿠키 사용
  timeout: 10000, // 10초
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 토큰 만료 → 로그인 페이지로 이동
      localStorage.removeItem("token");
      window.location.href = "/#/login";
    }
    return Promise.reject(error);
  }
);

// ------------------------
// 📌 1. 일기장 (Diary)
// ------------------------

export const createDiary = (name: string) =>
  api.post("/diary", { name });

export const getMyDiaries = () =>
  api.get("/diary");

export const getDiaryDetail = (id: number) =>
  api.get(`/diary/${id}`);

export const updateDiary = (id: number, name: string) =>
  api.put(`/diary/${id}`, { name });

export const deleteDiary = (id: number) =>
  api.delete(`/diary/${id}`);

// ------------------------
// 📌 2. 멤버 관리
// ------------------------

export const inviteMember = (diaryId: number, email: string) =>
  api.post(`/diary/${diaryId}/invite`, { email });

export const acceptInvite = (diaryId: number) =>
  api.post(`/diary/${diaryId}/accept`);

export const getDiaryMembers = (diaryId: number) =>
  api.get(`/diary/${diaryId}/members`);

export const removeMember = (diaryId: number, userId: number) =>
  api.delete(`/diary/${diaryId}/members/${userId}`);

export const getMyInvites = () => api.get('/diary/invites');

// ------------------------
// 📌 3. 일기 (Entry)
// ------------------------

export const createEntry = (data: {
  diaryId: number;
  title: string;
  content: string;
  emotion: string;
  tags: string[];
  imageUrl?: string;
}) => api.post("/entry", data);

export const getEntry = (id: number) =>
  api.get(`/entry/${id}`);

export const getEntries = (diaryId: number) =>
  api.get(`/diary/${diaryId}/entries`);

export const updateEntry = (
  id: number,
  data: { title: string; content: string; emotion: string; tags: string[] }
) => api.put(`/entry/${id}`, data);

export const deleteEntry = (id: number) =>
  api.delete(`/entry/${id}`);

// ------------------------
// 📌 4. 이미지 업로드/삭제
// ------------------------

export const uploadImage = (file: File) => {
  const formData = new FormData();
  formData.append("image", file);
  return api.post("/image/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

export const deleteImage = (url: string) =>
  api.delete("/image", { data: { url } });

// ------------------------
// 📌 5. 댓글
// ------------------------

export const createComment = (
  entryId: number,
  content: string,
  parentCommentId?: number
) =>
  api.post(`/entry/${entryId}/comment`, {
    content,
    parentCommentId: parentCommentId || null,
  });

export const getComments = (entryId: number) =>
  api.get(`/entry/${entryId}/comments`);

export const updateComment = (
  entryId: number,
  commentId: number,
  content: string
) =>
  api.put(`/entry/${entryId}/comments/${commentId}`, { content });

export const deleteComment = (entryId: number, commentId: number) =>
  api.delete(`/entry/${entryId}/comments/${commentId}`);

// ------------------------
// 📌 6. 태그
// ------------------------

export const createTag = (name: string) =>
  api.post("/tag", { name });

export const getTags = () =>
  api.get("/tag");

export const linkTags = (entryId: number, tagIds: number[]) =>
  api.post(`/entry/${entryId}/tags`, { tagIds });

export const unlinkTag = (entryId: number, tagId: number) =>
  api.delete(`/entry/${entryId}/tags/${tagId}`);

// ------------------------
// 📌 7. 인증/회원
// ------------------------

export const signup = (userId: string, password: string, nickname: string) =>
  api.post("/auth/signup", { userId, password, nickname });

export const login = (userId: string, password: string) =>
  api.post("/auth/login", { userId, password });

export const getMe = () => api.get("/auth/me");