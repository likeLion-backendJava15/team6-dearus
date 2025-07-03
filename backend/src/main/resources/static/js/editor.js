document.addEventListener("DOMContentLoaded", () => {
  const editor = new toastui.Editor({
    el: document.querySelector("#editor"),
    height: "500px",
    initialEditType: "wysiwyg",
    previewStyle: "vertical",
    hooks: {
      async addImageBlobHook(blob, callback) {
        const formData = new FormData();
        formData.append("image", blob);

        const response = await fetch("/image/upload", {
          method: "POST",
          body: formData,
        });

        const data = await response.json();

        const imageUrl = data.url;
        const editorInstance = editor.getCurrentModeEditor();
        editor.getCurrentModeEditor().insertHTML(`
        <p>
          <br class="ProseMirror-trailingBreak">
        </p>
      `);
        editor.getCurrentModeEditor().insertHTML(`
        <p><img src="${imageUrl}" alt="업로드 이미지" /></p>
      `);
        editor.getCurrentModeEditor().insertHTML(`
        <p>
          <br class="ProseMirror-trailingBreak">
        </p>
      `);

        callback(imageUrl, "업로드된 이미지");
      },
    },
  });
});
