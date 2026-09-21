<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Seller Dashboard — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .tabs { display: flex; gap: 8px; margin-bottom: 32px; border-bottom: 1px solid var(--line); }
        .tab-btn { background: none; border: none; padding: 12px 20px; font-size: 13px; letter-spacing: 0.04em;
            text-transform: uppercase; color: var(--muted); border-bottom: 2px solid transparent; }
        .tab-btn.active { color: var(--ink); border-color: var(--gold); }
        .tab-panel { display: none; }
        .tab-panel.active { display: block; }
        .stat-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 40px; }
        .stat-card { padding: 24px; background: var(--paper-dim); border-radius: var(--radius); }
        .stat-card .value { font-family: var(--font-display); font-size: 32px; margin: 8px 0 4px; }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container section">
    <p class="eyebrow">Seller dashboard</p>
    <h2 style="margin-bottom:32px;">Manage your shop</h2>

    <div class="stat-grid" id="stat-grid"></div>

    <div class="tabs">
        <button class="tab-btn active" data-tab="listings">Listings</button>
        <button class="tab-btn" data-tab="new-listing">Add listing</button>
        <button class="tab-btn" data-tab="incoming">Incoming orders</button>
    </div>

    <div class="tab-panel active" id="tab-listings">
        <div id="seller-listings"><p class="empty-state">Loading…</p></div>
    </div>

    <div class="tab-panel" id="tab-new-listing">
        <form id="new-listing-form" class="panel" style="max-width:560px;">
            <div class="field"><label for="p-name">Name</label><input id="p-name" required></div>
            <div class="field"><label for="p-desc">Description</label><textarea id="p-desc" rows="3"></textarea></div>
            <div class="field"><label for="p-price">Price (INR)</label><input id="p-price" type="number" step="0.01" min="0" required></div>
            <div class="field"><label for="p-compare-price">Compare-at price (optional — set to mark as on sale)</label><input id="p-compare-price" type="number" step="0.01" min="0"></div>
            <div class="field"><label for="p-stock">Stock quantity</label><input id="p-stock" type="number" min="0" required></div>
            <div class="field"><label for="p-category">Category</label><input id="p-category" required></div>
            <div class="field"><label for="p-image">Image URL</label><input id="p-image" type="url"></div>
            <p class="form-error" id="listing-error"></p>
            <button type="submit" class="btn btn-primary btn-block">Publish listing</button>
        </form>
    </div>

    <div class="tab-panel" id="tab-incoming">
        <div id="seller-orders"><p class="empty-state">Loading…</p></div>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>AdharshMart.initSellerDashboard();</script>
</body>
</html>
