<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Product — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <script>(function(){try{var t=localStorage.getItem("adharshmart-theme");if(t==="light"||t==="dark")document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
    <style>
        .pd-layout { display: grid; grid-template-columns: 1fr 1fr; gap: 64px; }
        /* Deliberately pure white, not var(--paper-dim) — product photography sits on a clean
           white backdrop regardless of light/dark theme, same convention as real e-commerce. */
        .pd-image { aspect-ratio: 4/5; background: #ffffff; overflow: hidden; }
        .pd-image img { width: 100%; height: 100%; object-fit: contain; }
        .pd-price { font-size: 28px; font-weight: 600; margin: 16px 0 24px; }

        .pd-variant-group { margin-top: 20px; }
        .pd-variant-label { font-size: 12px; letter-spacing: 0.08em; text-transform: uppercase; color: var(--muted); margin-bottom: 10px; }
        .pd-variant-label .color-name { color: var(--ink); text-transform: none; letter-spacing: normal; font-weight: 600; }

        .size-row { display: flex; flex-wrap: wrap; gap: 8px; }
        .size-box {
            min-width: 44px; height: 44px; padding: 0 12px;
            display: inline-flex; align-items: center; justify-content: center;
            border: 1px solid var(--line); border-radius: var(--radius);
            background: transparent; color: var(--ink);
            font-size: 13px; font-weight: 500; cursor: pointer;
            transition: border-color 0.2s ease, background-color 0.2s ease, color 0.2s ease;
        }
        .size-box:hover { border-color: var(--ink); }
        .size-box.active { background: var(--ink); border-color: var(--ink); color: var(--paper); }

        .color-row { display: flex; flex-wrap: wrap; gap: 12px; }
        .color-swatch {
            width: 30px; height: 30px; border-radius: 50%;
            border: none; padding: 0; cursor: pointer;
            box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.15);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .color-swatch:hover { transform: scale(1.1); }
        .color-swatch.active {
            box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.15), 0 0 0 2px var(--paper), 0 0 0 4px var(--gold-deep);
        }

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
