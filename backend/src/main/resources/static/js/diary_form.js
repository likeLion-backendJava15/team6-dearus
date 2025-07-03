document.addEventListener("DOMContentLoaded", () => {
  const editMode = window.editMode;
  const diaryId = window.diaryId;
  const submitBtn = document.getElementById("submitBtn");

  submitBtn.addEventListener("click", () => {
    const name = document.getElementById("diaryName").value.trim();
    if (!name) {
      alert("이름을 입력해주세요.");
      return;
    }

    const url = editMode ? `/api/diary/${diaryId}` : `/api/diary`;
    const method = editMode ? "PUT" : "POST";

    fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ name }),
    })
      .then((res) => {
        if (!res.ok) throw new Error("요청 실패");
        return res.json();
      })
      .then((data) => {
        alert(editMode ? "수정 완료" : "생성 완료");
        location.href = "/diary";
      })
      .catch((err) => alert(err.message));
  });
});
