<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>AdharshMart — Considered goods, well made</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<section class="hero">
    <div class="hero-motif" aria-hidden="true"></div>
    <div class="hero-content">
        <p class="eyebrow">New season — Autumn/Winter</p>
        <h1 class="display">Considered goods,<br>made to last.</h1>
        <p>A marketplace of independent sellers, curated the way a flagship store would be —
            wool, leather, and silk from people who still finish every seam by hand.</p>
        <div class="hero-actions">
            <a class="btn btn-primary" href="products.jsp">Shop the edit</a>
            <a class="btn" href="register.jsp?role=SELLER">Sell on AdharshMart</a>
        </div>
    </div>
</section>

<section class="section container">
    <div class="section-head">
        <div>
            <p class="eyebrow">Just in</p>
            <h2>Featured pieces</h2>
        </div>
        <a class="btn btn-outline" href="products.jsp">View all</a>
    </div>
    <div class="product-grid" id="featured-grid">
        <p class="empty-state">Loading…</p>
    </div>
</section>

<section class="section container" style="padding-top:0;">
    <div class="section-head">
        <div>
            <p class="eyebrow">Shop by category</p>
            <h2>Find your edit</h2>
        </div>
    </div>
    <div class="product-grid">
        <a class="product-card" href="products.jsp?category=Outerwear">
            <div class="thumb"><img src="https://picsum.photos/seed/adharshmart-wool-overcoat/700/875" alt="Outerwear"></div>
            <p class="name">Outerwear</p>
        </a>
        <a class="product-card" href="products.jsp?category=Footwear">
            <div class="thumb"><img src="https://picsum.photos/seed/adharshmart-court-sneaker/700/875" alt="Footwear"></div>
            <p class="name">Footwear</p>
        </a>
        <a class="product-card" href="products.jsp?category=Bags">
            <div class="thumb"><img src="https://picsum.photos/seed/adharshmart-leather-tote/700/875" alt="Bags"></div>
            <p class="name">Bags</p>
        </a>
        <a class="product-card" href="products.jsp?category=Knitwear">
            <div class="thumb"><img src="https://picsum.photos/seed/adharshmart-cashmere-crewneck/700/875" alt="Knitwear"></div>
            <p class="name">Knitwear</p>
        </a>
    </div>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>AdharshMart.renderFeatured('featured-grid');</script>
</body>
</html>
