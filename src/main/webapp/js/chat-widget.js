/**
 * Chat widget (Section 11 required architecture): floating button + panel, POST /api/v1/chat.
 * Depends on AdharshMart.api/el from app.js.
 */
(function () {
    document.addEventListener('DOMContentLoaded', () => {
        const toggle = document.getElementById('chat-toggle');
        const panel = document.getElementById('chat-panel');
        const form = document.getElementById('chat-form');
        const input = document.getElementById('chat-input');
        const messages = document.getElementById('chat-messages');
        if (!toggle || !panel || !form) return;

        const { api, el } = window.AdharshMart;

        toggle.addEventListener('click', () => {
            panel.classList.toggle('open');
            if (panel.classList.contains('open')) input.focus();
        });

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const text = input.value.trim();
            if (!text) return;
            appendMessage('user', text);
            input.value = '';
            input.disabled = true;

            const typing = appendMessage('bot', '…');
            const res = await api('/api/v1/chat', 'POST', { message: text });
            typing.remove();
            if (res.success) {
                appendMessage('bot', res.data.reply);
            } else {
                appendMessage('bot', res.error ? res.error.message : "Sorry, I couldn't process that.");
            }
            input.disabled = false;
            input.focus();
        });

        function appendMessage(role, text) {
            const node = el('div', { class: 'chat-msg ' + role }, [text]);
            messages.appendChild(node);
            messages.scrollTop = messages.scrollHeight;
            return node;
        }
    });
})();
