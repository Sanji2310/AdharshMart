<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Product — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .pd-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 64px; }
        .pd-image { aspect-ratio: 4/5; background: var(--paper-dim); overflow: hidden; }
        .pd-image img { width: 100%; height: 100%; object-fit: cover; }
        .pd-price { font-size: 28px; font-weight: 600; margin: 16px 0 24px; }
        .review-row { padding: 20px 0; border-bottom: 1px solid var(--line); }
        .review-row .meta { font-size: 12px; color: var(--muted); margin-top: 4px; }
        @media (max-width: 860px) { .pd-layout { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container section">
    <div class="pd-layout" id="pd-root">
        <p class="empty-state">Loading…</p>
    </div>

    <hr class="divider" style="margin-top:80px;">
    <h2 style="margin:48px 0 24px;">Reviews</h2>
    <div id="review-form-slot"></div>
    <div id="reviews-list"><p class="empty-state">No reviews yet.</p></div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>AdharshMart.initProductDetailPage();</script>
</body>
</html>
