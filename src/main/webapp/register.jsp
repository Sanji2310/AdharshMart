<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create account — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <script>(function(){try{var t=localStorage.getItem("adharshmart-theme");if(t==="light"||t==="dark")document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="form-shell">
        <p class="eyebrow">Join AdharshMart</p>
        <h1>Create your account</h1>
        <p class="subtitle">Shop as a buyer, or list your own pieces as a seller.</p>
        <form id="register-form">
            <div class="field">
                <label for="name">Full name</label>
                <input type="text" id="name" name="name" required>
            </div>
            <div class="field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required autocomplete="email">
            </div>
            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required minlength="8" autocomplete="new-password">
            </div>
            <div class="field">
                <label for="role">Account type</label>
                <select id="role" name="role">
                    <option value="BUYER">Buyer</option>
                    <option value="SELLER">Seller</option>
                </select>
            </div>
            <p class="form-error" id="form-error"></p>
            <button type="submit" class="btn btn-primary btn-block">Create account</button>
        </form>
        <p class="form-note">Already have an account? <a href="login.jsp" style="text-decoration:underline;">Sign in</a></p>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>
(() => {
    const params = new URLSearchParams(window.location.search);
    const roleParam = params.get('role');
    if (roleParam === 'SELLER') document.getElementById('role').value = 'SELLER';
})();
document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const errorEl = document.getElementById('form-error');
    errorEl.textContent = '';
    const body = {
        name: document.getElementById('name').value.trim(),
        email: document.getElementById('email').value.trim(),
        password: document.getElementById('password').value,
        role: document.getElementById('role').value
    };
    const res = await AdharshMart.api('/api/v1/auth/register', 'POST', body);
    if (res.success) {
        const loginRes = await AdharshMart.api('/api/v1/auth/login', 'POST', { email: body.email, password: body.password });
        window.location.href = loginRes.success ? 'index.jsp' : 'login.jsp';
    } else {
        errorEl.textContent = res.error ? res.error.message : 'Registration failed';
    }
});
</script>
</body>
</html>
