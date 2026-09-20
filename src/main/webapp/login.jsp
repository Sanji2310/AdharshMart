<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign in — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container">
    <div class="form-shell">
        <p class="eyebrow">Welcome back</p>
        <h1>Sign in</h1>
        <p class="subtitle">Access your orders, saved cart, and seller dashboard.</p>
        <form id="login-form">
            <div class="field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required autocomplete="email">
            </div>
            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required autocomplete="current-password">
            </div>
            <p class="form-error" id="form-error"></p>
            <button type="submit" class="btn btn-primary btn-block">Sign in</button>
        </form>
        <p class="form-note">New here? <a href="register.jsp" style="text-decoration:underline;">Create an account</a></p>
        <p class="form-note" style="margin-top:8px;">Demo: admin@adharshmart.com / AdminPass123! &middot;
            seller@adharshmart.com / SellerPass123! &middot; buyer@adharshmart.com / BuyerPass123!</p>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>
document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const errorEl = document.getElementById('form-error');
    errorEl.textContent = '';
    const body = {
        email: document.getElementById('email').value.trim(),
        password: document.getElementById('password').value
    };
    const res = await AdharshMart.api('/api/v1/auth/login', 'POST', body);
    if (res.success) {
        window.location.href = 'index.jsp';
    } else {
        errorEl.textContent = res.error ? res.error.message : 'Login failed';
    }
});
</script>
</body>
</html>
