// Функция для копирования текста в буфер обмена с использованием navigator.clipboard
async function copyToClipboard(text) {
    try {
        await navigator.clipboard.writeText(text);
        alert("Text copied to clipboard!");
    } catch (err) {
        alert("Failed to copy text: " + err);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.copy-btn').forEach(button => {
        button.addEventListener('click', function () {
            // Извлекаем id элемента, к которому привязана кнопка "Copy"
            const targetId = this.getAttribute('data-copy-target');
            const targetElement = document.getElementById(targetId);

            // Проверяем, что элемент найден и содержит текст
            if (targetElement) {
                const textToCopy = targetElement.textContent.trim();

                if (textToCopy) {
                    // Копируем текст
                    copyToClipboard(textToCopy);
                } else {
                    // Отправляем сообщение, если текст пустой
                    alert('The text is empty. Nothing to copy.');
                }
            } else {
                // Если элемент не найден
                alert('Element not found for copying.');
            }
        });
    });
});
