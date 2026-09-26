<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Checkout — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <script>(function(){try{var t=localStorage.getItem("adharshmart-theme");if(t==="light"||t==="dark")document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container section" style="max-width:720px;">
    <p class="eyebrow">Final step</p>
    <h2 style="margin-bottom:40px;">Checkout</h2>

    <div class="panel" style="margin-bottom:32px;">
        <h3 style="margin-bottom:16px;">Items</h3>
        <div id="checkout-lines"></div>
        <div id="checkout-summary" style="margin-top:16px;"></div>
    </div>

    <div class="panel">
        <h3 style="margin-bottom:8px;">Mock payment confirmation</h3>
        <p style="color:var(--muted); margin-bottom:20px;">This build implements a scope-constrained mock
            payment step — no card details are collected or transmitted. Clicking confirm places the order.</p>
        <button class="btn btn-gold btn-block" id="confirm-btn">Confirm mock payment &amp; place order</button>
        <p class="form-error" id="checkout-error"></p>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>AdharshMart.initCheckoutPage();</script>
</body>
</html>
