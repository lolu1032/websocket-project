    const Editor = toastui.Editor;
    const editor = new Editor({
        el: document.querySelector('#editor'),
        height: '600px',
        initialEditType: 'markdown',
        previewStyle: 'vertical',
        hooks: {
            addImageBlobHook: (blob, callback) => {
                const formData = new FormData();
                formData.append('file', blob);

                $.ajax({
                    type: 'POST',
                    enctype: 'multipart/form-data',
                    url: '/files/upload',
                    data: formData,
                    dataType: 'json',
                    processData: false,
                    contentType: false,
                    cache: false,
                    timeout: 600000,
                    success: function(data) {
                        const url = '/files/uploads/' + encodeURIComponent(data.fileName);
                        callback(url);
                        displayUploadedImage(url);
                    },
                    error: function(e) {
                        const errorImageUrl = '/path/to/default/image.png';
                        callback(errorImageUrl, '사진 대체 텍스트 입력');
                    }
                });
            }
        }
    });

    const languageInput = document.getElementById('languageInput');
    const modal = document.getElementById('languageModal');
    const closeBtn = document.querySelector('.close-btn');
    const saveBtn = document.querySelector('.save-btn');
    const checkboxes = document.querySelectorAll('input[name="lang"]');

    languageInput.addEventListener('click', () => {
        modal.style.display = 'block';
    });

    closeBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    saveBtn.addEventListener('click', (event) => {
        event.preventDefault();

        const selectedLanguages = Array.from(checkboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value);

        if (selectedLanguages.length > 0) {
            languageInput.value = selectedLanguages.join(', ');
        } else {
            languageInput.value = '';
        }

        modal.style.display = 'none';
    });

document.getElementById('postForm').addEventListener('submit', function(event) {
    event.preventDefault();  // 폼 제출 시 페이지 새로고침 방지

    const formData = new FormData(this);
    let markdownContent = editor.getMarkdown();

    formData.set('content', markdownContent);

    let headCount = formData.get('head-count');
    headCount = parseInt(headCount);
    if (isNaN(headCount)) {
        alert("인원수는 숫자만 입력할 수 있습니다.");
        return;
    }
    formData.set('head-count', headCount);

    fetch('/api/postSave', {
        method: 'POST',
        body: formData,
        credentials: 'include'
    })
    .then(response => {
        if (response.ok) {
            return response.text();
        }
        throw new Error('서버 응답이 올바르지 않습니다.');
    })
    .then(data => {
        console.log('성공:', data);
        alert("포스트가 성공적으로 저장되었습니다");
        window.location.href = '/';
    })
    .catch(error => {
        console.error('에러 발생:', error);
        alert("포스트 저장 중 오류가 발생했습니다.");
    });
});
