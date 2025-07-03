document.addEventListener("DOMContentLoaded", () => {
  const diaryId = window.diaryId;

  window.kickMember = function (diaryId, memberId) {
    if (!confirm("정말 추방하시겠습니까?")) return;

    fetch(`/api/diary/${diaryId}/members/${memberId}`, { method: "DELETE" })
      .then((res) => {
        if (!res.ok) throw new Error("추방 실패");
        alert("추방 완료");
        location.reload();
      })
      .catch((err) => alert(err.message));
  };

  window.sendInvite = function () {
    const email = document.getElementById("invite-email").value.trim();

    if (!email) {
      alert("이메일을 입력해주세요.");
      return;
    }

    fetch(`/api/diary/${diaryId}/invite`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
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
        document.getElementById("invite-email").value = "";
      })
      .catch((err) => {
        alert("❗Error : " + err.message);
      });
  };
});
