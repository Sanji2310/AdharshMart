<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .tabs { display: flex; gap: 8px; margin-bottom: 32px; border-bottom: 1px solid var(--line); }
        .tab-btn { background: none; border: none; padding: 12px 20px; font-size: 13px; letter-spacing: 0.04em;
            text-transform: uppercase; color: var(--muted); border-bottom: 2px solid transparent; }
        .tab-btn.active { color: var(--ink); border-color: var(--gold); }
        .tab-panel { display: none; }
        .tab-panel.active { display: block; }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container section">
    <p class="eyebrow">Administration</p>
    <h2 style="margin-bottom:32px;">Marketplace control</h2>

    <div class="tabs">
        <button class="tab-btn active" data-tab="users">Users</button>
        <button class="tab-btn" data-tab="orders">Orders</button>
    </div>

    <div class="tab-panel active" id="tab-users">
        <div id="admin-users"><p class="empty-state">Loading…</p></div>
    </div>
    <div class="tab-panel" id="tab-orders">
        <div id="admin-orders"><p class="empty-state">Loading…</p></div>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>AdharshMart.initAdminPage();</script>
</body>
</html>
