const dropZone = document.getElementById('dropZone');
const fileInput = document.getElementById('video');
const fileInfo = document.getElementById('fileInfo');
const fileName = document.getElementById('fileName');
const fileSize = document.getElementById('fileSize');
const uploadForm = document.getElementById('uploadForm');
const submitBtn = document.getElementById('submitBtn');
const loadingIndicator = document.getElementById('loadingIndicator');
const selectedTagsInput = document.getElementById('selectedTags');

// ===== ファイル選択 =====
fileInput.addEventListener('change', function () {
    if (this.files.length > 0) showFileInfo(this.files[0]);
});

function showFileInfo(file) {
    fileName.textContent = file.name;
    fileSize.textContent = `(${(file.size / 1024 / 1024).toFixed(1)} MB)`;
    fileInfo.classList.remove('d-none');
}

// ===== ドラッグ&ドロップ =====
dropZone.addEventListener('dragover', e => { e.preventDefault(); dropZone.classList.add('drag-over'); });
dropZone.addEventListener('dragleave', () => dropZone.classList.remove('drag-over'));
dropZone.addEventListener('drop', function (e) {
    e.preventDefault();
    dropZone.classList.remove('drag-over');
    if (e.dataTransfer.files.length > 0) {
        const dt = new DataTransfer();
        dt.items.add(e.dataTransfer.files[0]);
        fileInput.files = dt.files;
        showFileInfo(e.dataTransfer.files[0]);
    }
});

// ===== タグ選択 =====
const tagButtons = document.querySelectorAll('.tag-btn');
const selectedTags = new Set();

tagButtons.forEach(btn => {
    btn.addEventListener('click', function () {
        const tag = this.dataset.tag;
        if (selectedTags.has(tag)) {
            selectedTags.delete(tag);
            this.classList.remove('btn-success');
            this.classList.add('btn-outline-secondary');
        } else {
            selectedTags.add(tag);
            this.classList.remove('btn-outline-secondary');
            this.classList.add('btn-success');
        }
        // 選択タグをカンマ区切りで隠しフィールドに保存
        selectedTagsInput.value = Array.from(selectedTags).join(',');
    });
});

// ===== フォーム送信 =====
uploadForm.addEventListener('submit', function () {
    submitBtn.classList.add('d-none');
    loadingIndicator.classList.remove('d-none');
});
