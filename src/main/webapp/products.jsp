<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Shop — AdharshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <script>(function(){try{var t=localStorage.getItem("adharshmart-theme");if(t==="light"||t==="dark")document.documentElement.setAttribute("data-theme",t);}catch(e){}})();</script>
</head>
<body>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="container section">
    <div class="section-head">
        <div>
            <p class="eyebrow">The full collection</p>
            <h2 id="page-title">Shop all</h2>
        </div>
    </div>
    <form class="filters" id="filter-form">
        <input type="text" id="keyword" name="keyword" placeholder="Search products…">
        <select id="category" name="category">
            <option value="">All categories</option>
            <option value="Outerwear">Outerwear</option>
            <option value="Dresses">Dresses</option>
            <option value="Bags">Bags</option>
            <option value="Knitwear">Knitwear</option>
            <option value="Footwear">Footwear</option>
            <option value="Trousers">Trousers</option>
            <option value="Accessories">Accessories</option>
            <option value="Formalwear">Formalwear</option>
            <option value="Shirts">Shirts</option>
        </select>
        <label class="filter-check">
            <input type="checkbox" id="sale-only" name="saleOnly">
            On sale
        </label>
        <select id="sort" name="sort">
            <option value="">Sort: Featured</option>
            <option value="price-asc">Price: Low to High</option>
            <option value="price-desc">Price: High to Low</option>
            <option value="name-asc">Name: A&ndash;Z</option>
            <option value="newest">Newest</option>
        </select>
        <button type="submit" class="btn btn-outline">Filter</button>
    </form>
    <div class="product-grid" id="product-grid">
        <p class="empty-state">Loading…</p>
    </div>
</div>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
<%@ include file="/WEB-INF/jspf/chat-widget.jspf" %>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>AdharshMart.initProductsPage();</script>
</body>
</html>
