const entryId = window.entryId;
const token = window.token;
const loginMemberId = window.loginMemberId;

// 1. 댓글 목록 조회
async function loadComments() {
  console.log("📥 댓글을 불러옵니다");
  const res = await fetch(`/api/entry/${entryId}/comments`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  const data = await res.json();
  console.log("불러온 댓글:", data);
  renderComments(data);
}

// 2. 댓글 렌더링
function renderComments(comments) {
  const listDiv = document.getElementById("comment-list");
  listDiv.innerHTML = "";
  document.getElementById("comment-count").textContent =
    countAllComments(comments);

  if (comments.length === 0) {
    listDiv.innerHTML = "<p>아직 작성된 댓글이 없습니다.</p>";
    return;
  }

  // 재귀 렌더링 시작
  renderCommentList(comments, listDiv);
}

function countAllComments(comments) {
  let count = 0;
  comments.forEach((c) => {
    count += 1; // 자기 자신
    if (c.children && c.children.length > 0) {
      count += countAllComments(c.children); // 재귀 호출
    }
  });
  return count;
}

function renderCommentList(comments, parentElement) {
  comments.forEach((c) => {
    const commentDiv = document.createElement("div");
    commentDiv.className = "comment-item";
    commentDiv.setAttribute("data-comment-id", c.id);

    let buttons = `<button onclick="showReplyForm(${c.id})">답글</button>`;

    // 작성자인 경우에만 수정/삭제 버튼 보이기
    if (c.memberId === loginMemberId) {
      buttons += `
        <button onclick="editComment(${c.id}, '${c.content.replace(
        /'/g,
        "\\'"
      )}')">수정</button>
        <button onclick="deleteComment(${c.id})">삭제</button>
      `;
    }

    commentDiv.innerHTML = `
  <p><span class="comment-author">${c.memberNickname}</span>: ${c.content}</p>
  <div class="comment-btn-group" id="btn-group-${c.id}">
    ${buttons}
  </div>
  <div class="input-container" id="input-container-${c.id}"></div>
  <div id="children-${c.id}" class="reply-box"></div>
`;
    parentElement.appendChild(commentDiv);

    // 자식 댓글 재귀적으로 렌더링
    if (c.children && c.children.length > 0) {
      const childContainer = document.getElementById(`children-${c.id}`);
      renderCommentList(c.children, childContainer);
    }
  });
}

// 3. 댓글 작성
document
  .getElementById("comment-form")
  .addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    const content = form.content.value.trim();
    const parentCommentId = form.parentCommentId.value || null;

    if (!content) return;

    await fetch(`/api/entry/${entryId}/comments`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ content, parentCommentId }),
    });

    form.reset();
    loadComments();
  });

// 4. 댓글 수정
function editComment(commentId, oldContent) {
  const inputContainer = document.getElementById(
    `input-container-${commentId}`
  );
  if (!inputContainer) return;

  if (inputContainer.firstChild) {
    inputContainer.innerHTML = "";
    return;
  }

  document
    .querySelectorAll(".input-container")
    .forEach((div) => (div.innerHTML = ""));

  const form = document.createElement("form");
  form.className = "edit-form-temp";
  form.innerHTML = `
    <textarea name="newContent" class="reply-textarea" required>${oldContent}</textarea>
    <button type="submit" class="comm-wrt-btn">수정 완료</button>
  `;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const newContent = form.newContent.value.trim();
    if (!newContent || newContent === oldContent) return;

    await fetch(`/api/entry/${entryId}/comments/${commentId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ content: newContent }),
    });

    loadComments();
  });

  inputContainer.appendChild(form);
  form.querySelector("textarea").focus();
}

// 5. 댓글 삭제
async function deleteComment(id) {
  if (confirm("댓글을 삭제하시겠습니까?")) {
    await fetch(`/api/entry/${entryId}/comments/${id}`, {
      method: "DELETE",
    });
    loadComments();
  }
}

// 6. 답글 작성용
function showReplyForm(parentId) {
  const inputContainer = document.getElementById(`input-container-${parentId}`);
  if (!inputContainer) return;

  // 토글
  if (inputContainer.firstChild) {
    inputContainer.innerHTML = "";
    return;
  }

  // 다른 입력창 제거
  document.querySelectorAll(".input-container").forEach((div) => {
    div.innerHTML = "";
  });

  const form = document.createElement("form");
  form.id = "reply-form-temp";
  form.innerHTML = `
    <textarea name="content" class="reply-textarea" placeholder="답글을 입력하세요" required></textarea>
    <input type="hidden" name="parentCommentId" value="${parentId}" />
    <button type="submit" class="comm-wrt-btn">작성</button>
  `;

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const content = form.content.value.trim();
    const parentCommentId = form.parentCommentId.value;
    if (!content) return;

    await fetch(`/api/entry/${entryId}/comments`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ content, parentCommentId }),
    });

    loadComments();
  });

  inputContainer.appendChild(form);
  form.querySelector("textarea").focus();
}

// 💡 DOMContentLoaded 시점에 댓글 로딩
document.addEventListener("DOMContentLoaded", loadComments);
