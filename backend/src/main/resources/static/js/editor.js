const editor = new toastui.Editor({
  el: document.querySelector('#editor'),
  height: '500px',
  initialEditType: 'wysiwyg',
  previewStyle: 'vertical',
  hooks: {
    async addImageBlobHook(blob, callback) {
      const formData = new FormData();
      formData.append('image', blob);

      const response = await fetch('/image/upload', {
        method: 'POST',
        body: formData
      });

      const data = await response.json();
      callback(data.url, '업로드된 이미지');
    }
  }
});
