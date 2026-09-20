<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Your Bag — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container section">
    <p class="eyebrow">Your selection</p>
    <h2 style="margin-bottom:40px;">Shopping bag</h2>
    <div class="cart-layout">
        <div id="cart-lines"><p class="empty-state">Loading…</p></div>
        <div class="panel">
            <h3 style="margin-bottom:16px;">Order summary</h3>
            <div id="cart-summary"></div>
            <button class="btn btn-primary btn-block" id="checkout-btn" style="margin-top:20px;">Checkout</button>
            <p class="form-error" id="checkout-error"></p>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>AdharshMart.initCartPage();</script>
</body>
</html>
