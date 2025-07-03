// 전역 diaryId가 없으면 0으로 fallback
const globalDiaryId = typeof diaryId !== "undefined" ? diaryId : 0;

// 🔁 초대 폼 토글
function toggleInviteForm(diaryId) {
  const form = document.getElementById(`invite-form-${diaryId}`);
  if (form) {
    form.style.display = form.style.display === "none" ? "block" : "none";
  }
}

// 📧 초대 요청
function sendInvite(diaryId) {
  const emailInput = document.getElementById(`invite-email-${diaryId}`);
  const email = emailInput?.value.trim();

  if (!email) {
    alert("초대를 보낼 이메일을 입력해주세요.");
    return;
  }

  fetch(`/api/diary/${diaryId}/invite`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ email: email }),
  })
    .then(async (res) => {
      if (!res.ok) {
        let errorMessage = "초대 실패";
        try {
          const errorData = await res.json();
          if (errorData.message) errorMessage = errorData.message;
        } catch (err) {
          console.warn("JSON 파싱 실패:", err);
        }
        throw new Error(errorMessage);
      }
      alert("초대가 성공적으로 전송되었습니다.");
      emailInput.value = "";
      toggleInviteForm(diaryId);
    })
    .catch((err) => {
      alert("❗ Error : " + err.message);
    });
}

// 🗑️ 일기장 삭제 요청
function deleteDiary(diaryId) {
  if (!confirm("정말 일기장을 삭제하시겠습니까?")) return;

  fetch(`/api/diary/${diaryId}`, {
    method: "DELETE",
  })
    .then((res) => {
      if (!res.ok) throw new Error("삭제 실패");
      return res.status === 204 ? null : res.json();
    })
    .then(() => {
      alert("삭제 완료");
      location.reload();
    })
    .catch((err) => alert(err.message));
}
