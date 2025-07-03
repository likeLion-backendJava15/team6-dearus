function deleteEntry(entryId, diaryId) {
  if (!confirm("정말 삭제하시겠습니까?")) return;

  fetch(`/api/entry/${entryId}`, {
    method: "DELETE",
  })
    .then((res) => {
      if (!res.ok) throw new Error("삭제 실패");
      return res.json();
    })
    .then(() => {
      alert("삭제 완료");
      location.href = `/entry/list?diaryId=${diaryId}`;
    })
    .catch((err) => alert(err.message));
}

// 이모지 맵핑 함수 (필요 시 전역으로 사용 가능)
function getEmotionEmoji(emotion) {
  const map = {
    행복해: "😊",
    즐거워: "😄",
    감사해: "🙏",
    사랑해: "❤️",
    뿌듯해: "😌",
    그저그래: "😐",
    화나: "😡",
    힘들어: "😫",
  };
  return map[emotion] || "";
}
