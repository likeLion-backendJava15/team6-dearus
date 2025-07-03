let editor;
let tags = [];

document.addEventListener("DOMContentLoaded", () => {
  const entryDataDiv = document.getElementById("entry-data");
  const initialContent = entryDataDiv?.dataset?.content || "";
  let initialTags = [];
  try {
    initialTags = JSON.parse(entryDataDiv?.dataset?.tags || "[]");
  } catch (e) {
    console.warn("태그 JSON 파싱 실패", e);
  }

  editor = new toastui.Editor({
    el: document.querySelector("#editor"),
    height: "500px",
    initialEditType: "wysiwyg",
    previewStyle: "vertical",
    initialValue: initialContent,
    hooks: {
      addImageBlobHook: (blob, callback) => {
        const formData = new FormData();
        formData.append("image", blob);
        fetch("/image/upload", {
          method: "POST",
          body: formData,
        })
          .then((res) => res.json())
          .then((data) => callback(data.url, "업로드 이미지"))
          .catch(() => alert("이미지 업로드 실패"));
      },
    },
  });

  const tagInput = document.getElementById("tag-input");
  tagInput.addEventListener("keyup", function (e) {
    if (e.key === "Enter") {
      e.preventDefault();
      const value = e.target.value.trim();
      if (value && !tags.includes(value)) {
        tags.push(value);
        renderTags();
        e.target.value = "";
      }
    }
  });

  if (window.isEdit && initialContent) {
    tags.push(...(initialTags || []));
    renderTags();
  }

  const saveBtn = document.querySelector(".entry-btn");
  saveBtn.addEventListener("click", submitEntry);
});

function renderTags() {
  const container = document.getElementById("tag-container");
  container.innerHTML = "";
  tags.forEach((tag) => {
    const tagEl = document.createElement("div");
    tagEl.className = "tag-item";

    const label = document.createElement("span");
    label.textContent = tag;

    const deleteBtn = document.createElement("button");
    deleteBtn.className = "tag-btn";
    deleteBtn.innerHTML = "&times;";
    deleteBtn.onclick = () => {
      const index = tags.indexOf(tag);
      if (index !== -1) {
        tags.splice(index, 1);
        renderTags();
      }
    };

    tagEl.appendChild(label);
    tagEl.appendChild(deleteBtn);
    container.appendChild(tagEl);
  });
}

function submitEntry(event) {
  event.preventDefault();

  const title = document.getElementById("title").value.trim();
  const emotion = document.getElementById("emotion").value;
  const diaryId = parseInt(document.getElementById("diaryId").value);
  const entryId = window.entryId || 0;
  const isEdit = window.isEdit || false;

  if (!title) {
    alert("제목을 입력해주세요.");
    document.getElementById("title").focus();
    return;
  }
  if (!diaryId) {
    alert("일기장 ID가 없습니다.");
    return;
  }

  const data = {
    title: title,
    content: editor.getHTML(),
    emotion: emotion,
    diaryId: diaryId,
    imageUrl: null,
    tags: tags,
  };

  const method = isEdit ? "PUT" : "POST";
  const url = isEdit ? `/api/entry/${entryId}` : "/api/entry";

  fetch(url, {
    method: method,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((res) => {
      if (!res.ok) throw new Error("요청 실패");
      return res.json();
    })
    .then(() => {
      alert("저장 완료!");
      location.href = `/entry/list?diaryId=${diaryId}`;
    })
    .catch((err) => {
      alert("저장 실패: " + err.message);
    });
}
