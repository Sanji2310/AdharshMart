<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>AdharshMart — Considered goods, well made</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <script>(function(){try{var t=localStorage.getItem("adharshmart-theme");if(t==="light"||t==="dark")document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
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

<section class="category-banner">
    <img src="https://images.unsplash.com/photo-1777448067492-b665d9e0b29c?q=80&amp;w=1600&amp;fit=crop&amp;auto=format" alt="Outerwear, the Autumn/Winter edit">
    <div class="category-banner-copy">
        <p class="eyebrow">The Autumn/Winter edit</p>
        <h2 class="display">Outerwear, made for the season</h2>
        <a class="btn btn-primary" href="products.jsp?category=Outerwear">Shop Outerwear</a>
    </div>
</section>

<section class="section container sale-section" style="padding-top:0;">
    <div class="section-head">
        <div>
            <p class="eyebrow">Limited time</p>
            <h2>On sale</h2>
        </div>
        <a class="btn btn-outline" href="products.jsp">View all</a>
    </div>
    <div class="product-grid" id="sale-grid">
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
            <div class="thumb"><img src="https://images.unsplash.com/photo-1539533113208-f6df8cc8b543?q=80&amp;w=700&amp;fit=crop&amp;auto=format" alt="Outerwear"></div>
            <p class="name">Outerwear</p>
        </a>
        <a class="product-card" href="products.jsp?category=Footwear">
            <div class="thumb"><img src="https://images.unsplash.com/photo-1608379743498-ac08f6d022ba?q=80&amp;w=700&amp;fit=crop&amp;auto=format" alt="Footwear"></div>
            <p class="name">Footwear</p>
        </a>
        <a class="product-card" href="products.jsp?category=Bags">
            <div class="thumb"><img src="https://images.unsplash.com/photo-1624687943971-e86af76d57de?q=80&amp;w=700&amp;fit=crop&amp;auto=format" alt="Bags"></div>
            <p class="name">Bags</p>
        </a>
        <a class="product-card" href="products.jsp?category=Knitwear">
            <div class="thumb"><img src="https://images.unsplash.com/photo-1604573824419-289a9a10672c?q=80&amp;w=700&amp;fit=crop&amp;auto=format" alt="Knitwear"></div>
            <p class="name">Knitwear</p>
        </a>
    </div>
</section>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>
    AdharshMart.renderFeatured('featured-grid');
    AdharshMart.renderSale('sale-grid');
</script>
</body>
</html>
