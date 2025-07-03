const API = '/api';

document.addEventListener('DOMContentLoaded', () => {
  loadTags();
  document.getElementById('createForm').addEventListener('submit', handleCreate);
});

async function loadTags(selectedTagId) {
  const res = await fetch(`${API}/tag`);
  const tags = await res.json();
  const list = tags.map(tag => {
    const selectedStyle = tag.id === selectedTagId ? 'font-weight:bold;' : '';
    return `
      <li>
        <span style="${selectedStyle}" onclick="onTagClick(${tag.id}, '${tag.name}')">${tag.name}</span>
        <div class="actions">
          <button onclick="startEdit(${tag.id}, '${tag.name}')">수정</button>
          <button onclick="removeTag(${tag.id})">삭제</button>
        </div>
      </li>`;
  }).join('');
  document.getElementById('tagList').innerHTML = list;
}

async function handleCreate(e) {
  e.preventDefault();
  const name = document.getElementById('newTagName').value.trim();
  if (!name) return;
  await fetch(`${API}/tag`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name })
  });
  document.getElementById('newTagName').value = '';
  loadTags();
  document.getElementById('entriesSection').style.display = 'none';
}

let editingId = null;

function startEdit(id, name) {
  editingId = id;
  const li = [...document.querySelectorAll('#tagList li')]
    .find(node => node.textContent.includes(name));
  li.innerHTML = `
    <input id="editName" value="${name}" />
    <button onclick="saveEdit()">저장</button>
    <button onclick="cancelEdit()">취소</button>
  `;
}

async function saveEdit() {
  const name = document.getElementById('editName').value.trim();
  if (!name) return;
  await fetch(`${API}/tag/${editingId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name })
  });
  editingId = null;
  loadTags();
}

function cancelEdit() {
  editingId = null;
  loadTags();
}

async function removeTag(id) {
  if (!confirm('정말 삭제하시겠습니까?')) return;
  await fetch(`${API}/tag/${id}`, { method: 'DELETE' });
  loadTags();
  document.getElementById('entriesSection').style.display = 'none';
}

async function onTagClick(id, name) {
  loadTags(id);
  const res = await fetch(`${API}/tag/${id}/entries`);
  const entries = await res.json();
  document.getElementById('entriesHeader').textContent = `#${name} 태그 일기 목록`;
  const entriesHtml = entries.length
    ? entries.map(e => `
        <li>
          <h3>${e.title}</h3>
          <p>${e.content}</p>
        </li>`).join('')
    : '<li>해당 태그가 달린 일기가 없습니다.</li>';
  document.getElementById('entriesList').innerHTML = entriesHtml;
  document.getElementById('entriesSection').style.display = 'block';
}
