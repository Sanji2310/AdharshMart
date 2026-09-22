/**
 * AdharshMart front-end — vanilla JS + fetch() against the /api/v1 JSON API (Section 2, 13).
 * No template-string HTML injection for user-supplied fields: everything user-authored (names,
 * descriptions, reviews) goes through textContent, so escaping happens by construction —
 * the browser-side analogue of the JSTL <c:out> rule enforced server-side in the JSPs.
 */
(function (global) {
    // Set server-side (JSP EL) in WEB-INF/jspf/header.jspf, included on every page.
    const CTX = typeof window.APP_CONTEXT_PATH === 'string' ? window.APP_CONTEXT_PATH : '';

    async function api(path, method, body) {
        try {
            const res = await fetch(CTX + path, {
                method: method || 'GET',
                headers: body ? { 'Content-Type': 'application/json' } : {},
                body: body ? JSON.stringify(body) : undefined,
                credentials: 'same-origin'
            });
            const text = await res.text();
            const json = text ? JSON.parse(text) : { success: res.ok, data: null, error: null };
            return json;
        } catch (err) {
            return { success: false, data: null, error: { code: 'NETWORK_ERROR', message: 'Network error — please try again.' } };
        }
    }

    function el(tag, attrs, children) {
        const node = document.createElement(tag);
        if (attrs) {
            for (const [key, value] of Object.entries(attrs)) {
                if (key === 'class') node.className = value;
                else if (key === 'html') node.innerHTML = value; // only ever used with our own static strings
                else if (key.startsWith('on')) node.addEventListener(key.substring(2), value);
                else node.setAttribute(key, value);
            }
        }
        (children || []).forEach((c) => {
            if (c == null) return;
            node.appendChild(typeof c === 'string' ? document.createTextNode(c) : c);
        });
        return node;
    }

    function money(amount) {
        const n = typeof amount === 'number' ? amount : parseFloat(amount || 0);
        return '₹' + new Intl.NumberFormat('en-IN', { maximumFractionDigits: 0 }).format(n);
    }

    function stars(rating) {
        const full = Math.round(rating || 0);
        return '★'.repeat(full) + '☆'.repeat(5 - full);
    }

    function toast(message) {
        let node = document.querySelector('.toast');
        if (!node) {
            node = el('div', { class: 'toast' }, []);
            document.body.appendChild(node);
        }
        node.textContent = message;
        requestAnimationFrame(() => node.classList.add('show'));
        setTimeout(() => node.classList.remove('show'), 2600);
    }

    function isOnSale(p) {
        return p.compareAtPrice != null && parseFloat(p.compareAtPrice) > parseFloat(p.price);
    }

    function priceBlock(p) {
        if (!isOnSale(p)) {
            return el('p', { class: 'price' }, [money(p.price)]);
        }
        return el('p', { class: 'price price-sale' }, [
            el('span', { class: 'price-now' }, [money(p.price)]),
            el('span', { class: 'price-was' }, [money(p.compareAtPrice)])
        ]);
    }

    function productCard(p) {
        const thumb = el('div', { class: 'thumb' }, [
            el('img', { src: p.imageUrl || '', alt: p.name, loading: 'lazy' })
        ]);
        if (p.stockQty <= 0) {
            thumb.appendChild(el('span', { class: 'badge-oos' }, ['Sold out']));
        } else if (isOnSale(p)) {
            const pct = Math.round((1 - parseFloat(p.price) / parseFloat(p.compareAtPrice)) * 100);
            thumb.appendChild(el('span', { class: 'badge-sale' }, ['−' + pct + '%']));
        }
        return el('a', { class: 'product-card', href: 'product-detail.jsp?id=' + p.id }, [
            thumb,
            el('p', { class: 'category' }, [p.category]),
            el('p', { class: 'name' }, [p.name]),
            priceBlock(p),
            p.reviewCount > 0 ? el('p', { class: 'rating' }, [stars(p.averageRating) + ' (' + p.reviewCount + ')']) : null
        ]);
    }

    async function renderFeatured(containerId) {
        const container = document.getElementById(containerId);
        const res = await api('/api/v1/products', 'GET');
        container.innerHTML = '';
        if (!res.success || !res.data || res.data.length === 0) {
            container.appendChild(el('p', { class: 'empty-state' }, ['No products yet — check back soon.']));
            return;
        }
        res.data.slice(0, 8).forEach((p) => container.appendChild(productCard(p)));
    }

    async function renderSale(containerId) {
        const container = document.getElementById(containerId);
        const section = container ? container.closest('.sale-section') : null;
        const res = await api('/api/v1/products', 'GET');
        if (!container) return;
        container.innerHTML = '';
        const onSale = res.success && res.data ? res.data.filter(isOnSale) : [];
        if (onSale.length === 0) {
            if (section) section.style.display = 'none';
            return;
        }
        onSale.slice(0, 4).forEach((p) => container.appendChild(productCard(p)));
    }

    async function initProductsPage() {
        const grid = document.getElementById('product-grid');
        const params = new URLSearchParams(window.location.search);
        const keywordInput = document.getElementById('keyword');
        const categorySelect = document.getElementById('category');
        if (params.get('keyword')) keywordInput.value = params.get('keyword');
        if (params.get('category')) categorySelect.value = params.get('category');

        async function load() {
            const keyword = keywordInput.value.trim();
            const category = categorySelect.value;
            const qs = new URLSearchParams();
            if (keyword) qs.set('keyword', keyword);
            if (category) qs.set('category', category);
            grid.innerHTML = '';
            const res = await api('/api/v1/products' + (qs.toString() ? '?' + qs.toString() : ''), 'GET');
            if (!res.success || !res.data || res.data.length === 0) {
                grid.appendChild(el('p', { class: 'empty-state' }, ['No products match your search.']));
                return;
            }
            res.data.forEach((p) => grid.appendChild(productCard(p)));
        }

        document.getElementById('filter-form').addEventListener('submit', (e) => {
            e.preventDefault();
            load();
        });
        load();
    }

    async function initProductDetailPage() {
        const params = new URLSearchParams(window.location.search);
        const id = params.get('id');
        const root = document.getElementById('pd-root');
        if (!id) {
            root.innerHTML = '';
            root.appendChild(el('p', { class: 'empty-state' }, ['Product not found.']));
            return;
        }
        const res = await api('/api/v1/products/' + id, 'GET');
        root.innerHTML = '';
        if (!res.success) {
            root.appendChild(el('p', { class: 'empty-state' }, [res.error ? res.error.message : 'Product not found.']));
            return;
        }
        const p = res.data;
        document.title = p.name + ' — AdharshMart';

        const selection = { size: null, color: (p.colors && p.colors.length > 0) ? p.colors[0].name : null };
        const sizeErrorEl = el('p', { class: 'form-error', style: 'margin-top:8px;display:none;' }, ['Please select a size']);

        const qtyState = { value: 1 };
        const qtyLabel = el('span', {}, [String(qtyState.value)]);
        const addBtn = el('button', {
            class: 'btn btn-primary btn-block', style: 'margin-top:24px;',
            onclick: async () => {
                if (p.sizes && p.sizes.length > 0 && !selection.size) {
                    sizeErrorEl.style.display = 'block';
                    return;
                }
                const r = await api('/api/v1/cart', 'POST', { productId: p.id, quantity: qtyState.value });
                if (r.success) { toast('Added to bag'); updateCartBadge(); }
                else toast(r.error ? r.error.message : 'Could not add to bag');
            }
        }, [p.stockQty > 0 ? 'Add to bag' : 'Notify me']);
        if (p.stockQty <= 0) addBtn.disabled = true;

        const sizeSelector = (p.sizes && p.sizes.length > 0) ? el('div', { class: 'pd-variant-group' }, [
            el('p', { class: 'pd-variant-label' }, ['Size']),
            el('div', { class: 'size-row' }, p.sizes.map((label) => el('button', {
                type: 'button',
                class: 'size-box',
                onclick: (e) => {
                    selection.size = label;
                    sizeErrorEl.style.display = 'none';
                    Array.from(e.currentTarget.parentElement.children).forEach((c) => c.classList.remove('active'));
                    e.currentTarget.classList.add('active');
                }
            }, [label]))),
            sizeErrorEl
        ]) : null;

        const colorNameLabel = el('span', { class: 'color-name' }, [selection.color || '']);
        const colorSelector = (p.colors && p.colors.length > 0) ? el('div', { class: 'pd-variant-group' }, [
            el('p', { class: 'pd-variant-label' }, ['Color — ', colorNameLabel]),
            el('div', { class: 'color-row' }, p.colors.map((c, i) => el('button', {
                type: 'button',
                class: 'color-swatch' + (i === 0 ? ' active' : ''),
                style: 'background-color:' + c.hex + ';',
                title: c.name,
                'aria-label': c.name,
                onclick: (e) => {
                    selection.color = c.name;
                    colorNameLabel.textContent = c.name;
                    Array.from(e.currentTarget.parentElement.children).forEach((el2) => el2.classList.remove('active'));
                    e.currentTarget.classList.add('active');
                }
            }, [])))
        ]) : null;

        const saveBtn = el('button', {
            class: 'btn btn-outline btn-block', style: 'margin-top:12px;',
            onclick: async () => {
                if (!getSessionUserId()) { window.location.href = 'login.jsp'; return; }
                const r = await api('/api/v1/wishlist', 'POST', { productId: p.id });
                toast(r.success ? 'Saved to wishlist' : (r.error ? r.error.message : 'Could not save'));
            }
        }, ['Save for later']);

        root.appendChild(el('div', { class: 'pd-image' }, [el('img', { src: p.imageUrl || '', alt: p.name })]));
        root.appendChild(el('div', {}, [
            el('p', { class: 'category' }, [p.category]),
            el('h1', { class: 'display', style: 'font-size:36px;margin:12px 0;' }, [p.name]),
            p.reviewCount > 0 ? el('p', { class: 'rating' }, [stars(p.averageRating) + ' · ' + p.reviewCount + ' review(s)']) : null,
            isOnSale(p)
                ? el('p', { class: 'pd-price price-sale' }, [
                    el('span', { class: 'price-now' }, [money(p.price)]),
                    el('span', { class: 'price-was' }, [money(p.compareAtPrice)])
                ])
                : el('p', { class: 'pd-price' }, [money(p.price)]),
            el('p', { style: 'color:var(--muted);line-height:1.7;' }, [p.description || '']),
            colorSelector,
            sizeSelector,
            el('div', { class: 'qty-control', style: 'margin-top:24px;' }, [
                el('button', { type: 'button', onclick: () => { if (qtyState.value > 1) { qtyState.value--; qtyLabel.textContent = qtyState.value; } } }, ['−']),
                qtyLabel,
                el('button', { type: 'button', onclick: () => { qtyState.value++; qtyLabel.textContent = qtyState.value; } }, ['+'])
            ]),
            addBtn,
            saveBtn
        ]));

        renderReviewForm(p.id);
        renderReviews(p.id);
    }

    async function renderReviewForm(productId) {
        const slot = document.getElementById('review-form-slot');
        const userId = getSessionUserId();
        if (!userId) {
            slot.appendChild(el('p', { style: 'color:var(--muted);margin-bottom:24px;' }, ['Sign in and complete a delivered order to leave a review.']));
            return;
        }
        const ratingSelect = el('select', {}, [1, 2, 3, 4, 5].map((n) => el('option', { value: n }, [n + ' star' + (n > 1 ? 's' : '')])));
        ratingSelect.value = '5';
        const commentInput = el('textarea', { rows: 3, placeholder: 'Share your experience…' }, []);
        const errorEl = el('p', { class: 'form-error' }, []);
        const form = el('form', {
            class: 'panel', style: 'max-width:480px;margin-bottom:24px;',
            onsubmit: async (e) => {
                e.preventDefault();
                errorEl.textContent = '';
                const res = await api('/api/v1/reviews', 'POST', {
                    productId: Number(productId), rating: Number(ratingSelect.value), comment: commentInput.value.trim()
                });
                if (res.success) {
                    commentInput.value = '';
                    toast('Review submitted');
                    renderReviews(productId);
                } else {
                    errorEl.textContent = res.error ? res.error.message : 'Could not submit review';
                }
            }
        }, [
            el('div', { class: 'field' }, [el('label', {}, ['Rating']), ratingSelect]),
            el('div', { class: 'field' }, [el('label', {}, ['Comment']), commentInput]),
            errorEl,
            el('button', { type: 'submit', class: 'btn btn-outline' }, ['Submit review'])
        ]);
        slot.appendChild(form);
    }

    async function renderReviews(productId) {
        const list = document.getElementById('reviews-list');
        const res = await api('/api/v1/reviews?productId=' + productId, 'GET');
        list.innerHTML = '';
        if (!res.success || !res.data || res.data.length === 0) {
            list.appendChild(el('p', { class: 'empty-state' }, ['No reviews yet — be the first.']));
            return;
        }
        res.data.forEach((r) => {
            list.appendChild(el('div', { class: 'review-row' }, [
                el('p', { class: 'stars' }, [stars(r.rating)]),
                el('p', {}, [r.comment || '']),
                el('p', { class: 'meta' }, [r.reviewerName])
            ]));
        });
    }

    function getSessionUserId() {
        return typeof window.APP_SESSION_USER_ID !== 'undefined' ? window.APP_SESSION_USER_ID : null;
    }

    async function updateCartBadge() {
        const badge = document.getElementById('cart-count');
        if (!badge) return;
        const res = await api('/api/v1/cart', 'GET');
        badge.textContent = res.success && res.data ? String(res.data.items.length) : '0';
    }

    function wireLogout() {
        const btn = document.getElementById('logout-btn');
        if (btn) {
            btn.addEventListener('click', async () => {
                await api('/api/v1/auth/logout', 'POST', {});
                window.location.href = 'index.jsp';
            });
        }
    }

    // ---------------- Sales chart (admin + seller dashboards) ----------------
    // Aggregates whatever order list the caller already fetched into per-day revenue buckets —
    // no dedicated analytics endpoint needed, both dashboards already load their full order list.
    function aggregateByDay(records, days, amountFn) {
        const buckets = [];
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        for (let i = days - 1; i >= 0; i--) {
            const d = new Date(today);
            d.setDate(d.getDate() - i);
            buckets.push({ date: d, value: 0 });
        }
        records.forEach((r) => {
            const created = new Date(r.createdAt);
            created.setHours(0, 0, 0, 0);
            const bucket = buckets.find((b) => b.date.getTime() === created.getTime());
            if (bucket) bucket.value += amountFn(r);
        });
        return buckets;
    }

    function renderSalesChart(container, buckets, title) {
        container.innerHTML = '';
        const total = buckets.reduce((s, b) => s + b.value, 0);
        const wrap = el('div', { class: 'sales-chart-wrap' }, [
            el('div', { class: 'chart-head' }, [
                el('p', { class: 'eyebrow' }, [title]),
                el('p', { class: 'chart-total' }, [money(total)])
            ])
        ]);
        if (total === 0) {
            wrap.appendChild(el('p', { class: 'empty-state' }, ['No sales in this period yet.']));
            container.appendChild(wrap);
            return;
        }

        const svgNS = 'http://www.w3.org/2000/svg';
        const w = 720, h = 180, padX = 8, padY = 16;
        const max = Math.max(...buckets.map((b) => b.value), 1);
        const stepX = (w - padX * 2) / (buckets.length - 1 || 1);
        const points = buckets.map((b, i) => [
            padX + i * stepX,
            padY + (h - padY * 2) * (1 - b.value / max)
        ]);

        const svg = document.createElementNS(svgNS, 'svg');
        svg.setAttribute('viewBox', `0 0 ${w} ${h}`);
        svg.setAttribute('preserveAspectRatio', 'none');
        svg.setAttribute('class', 'sales-chart-svg');

        [0.25, 0.5, 0.75].forEach((f) => {
            const y = padY + (h - padY * 2) * f;
            const gridLine = document.createElementNS(svgNS, 'line');
            gridLine.setAttribute('x1', padX); gridLine.setAttribute('x2', w - padX);
            gridLine.setAttribute('y1', y); gridLine.setAttribute('y2', y);
            gridLine.setAttribute('class', 'chart-grid');
            svg.appendChild(gridLine);
        });

        const linePath = 'M' + points.map((p) => p.join(',')).join(' L');
        const areaPath = linePath + ` L${points[points.length - 1][0]},${h - padY} L${points[0][0]},${h - padY} Z`;

        const area = document.createElementNS(svgNS, 'path');
        area.setAttribute('d', areaPath);
        area.setAttribute('class', 'chart-area');
        svg.appendChild(area);

        const line = document.createElementNS(svgNS, 'path');
        line.setAttribute('d', linePath);
        line.setAttribute('class', 'chart-line');
        svg.appendChild(line);

        points.forEach(([x, y], i) => {
            const dot = document.createElementNS(svgNS, 'circle');
            dot.setAttribute('cx', String(x)); dot.setAttribute('cy', String(y)); dot.setAttribute('r', '2.5');
            dot.setAttribute('class', 'chart-dot');
            const dotTitle = document.createElementNS(svgNS, 'title');
            dotTitle.textContent = buckets[i].date.toLocaleDateString('en-IN', { month: 'short', day: 'numeric' }) + ': ' + money(buckets[i].value);
            dot.appendChild(dotTitle);
            svg.appendChild(dot);
        });

        wrap.appendChild(svg);

        const labelEvery = Math.max(1, Math.ceil(buckets.length / 6));
        wrap.appendChild(el('div', { class: 'chart-labels' },
            buckets.filter((_, i) => i % labelEvery === 0).map((b) =>
                el('span', {}, [b.date.toLocaleDateString('en-IN', { month: 'short', day: 'numeric' })]))
        ));
        container.appendChild(wrap);
    }

    // Theme toggle: explicit choice wins over the OS's prefers-color-scheme once the visitor
    // has picked one, persisted so it survives across pages/visits. Unset (no localStorage entry)
    // means "follow system", handled entirely by the CSS media query in main.css.
    const THEME_KEY = 'adharshmart-theme';
    function applyTheme(theme) {
        const root = document.documentElement;
        if (theme === 'light' || theme === 'dark') {
            root.setAttribute('data-theme', theme);
        } else {
            root.removeAttribute('data-theme');
        }
        const btn = document.getElementById('theme-toggle');
        if (btn) {
            const systemDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
            const isDark = theme === 'dark' || (theme !== 'light' && systemDark);
            btn.innerHTML = isDark ? '&#9788;' : '&#9789;';
            btn.setAttribute('aria-label', isDark ? 'Switch to light mode' : 'Switch to dark mode');
        }
    }
    function wireThemeToggle() {
        applyTheme(localStorage.getItem(THEME_KEY));
        const btn = document.getElementById('theme-toggle');
        if (!btn) return;
        btn.addEventListener('click', () => {
            const systemDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
            const current = localStorage.getItem(THEME_KEY) || (systemDark ? 'dark' : 'light');
            const next = current === 'dark' ? 'light' : 'dark';
            localStorage.setItem(THEME_KEY, next);
            applyTheme(next);
        });
    }

    function wireTabs() {
        document.querySelectorAll('.tab-btn').forEach((btn) => {
            btn.addEventListener('click', () => {
                document.querySelectorAll('.tab-btn').forEach((b) => b.classList.remove('active'));
                document.querySelectorAll('.tab-panel').forEach((p) => p.classList.remove('active'));
                btn.classList.add('active');
                document.getElementById('tab-' + btn.dataset.tab).classList.add('active');
            });
        });
    }

    // Image quality: a failed/slow product image degrades to a clean neutral panel (the same
    // --paper-dim background every .thumb already shows while loading) instead of the browser's
    // broken-image glyph + alt text — 'error' doesn't bubble, so this listens in the capture phase.
    document.addEventListener('error', (e) => {
        if (e.target.tagName === 'IMG') {
            e.target.style.visibility = 'hidden';
        }
    }, true);

    document.addEventListener('DOMContentLoaded', () => {
        wireLogout();
        wireTabs();
        wireThemeToggle();
        updateCartBadge();
    });

    global.AdharshMart = {
        api, el, money, stars, toast, productCard, renderFeatured, renderSale,
        initProductsPage, initProductDetailPage, updateCartBadge,
        initCartPage: () => global.AdharshMartCart && global.AdharshMartCart.initCartPage(),
        initCheckoutPage: () => global.AdharshMartCart && global.AdharshMartCart.initCheckoutPage(),
        initOrdersPage,
        initSellerDashboard,
        initAdminPage,
        initWishlistPage
    };

    // ---------------- Order history (F6) ----------------
    async function initOrdersPage() {
        const list = document.getElementById('orders-list');
        const res = await api('/api/v1/orders', 'GET');
        list.innerHTML = '';
        if (!res.success) {
            list.appendChild(el('p', { class: 'empty-state' }, [res.error ? res.error.message : 'Sign in to view your orders.']));
            return;
        }
        if (res.data.length === 0) {
            list.appendChild(el('p', { class: 'empty-state' }, ['No orders yet — your history will appear here.']));
            return;
        }
        res.data.forEach((order) => list.appendChild(orderCard(order)));
    }

    // ---------------- Wishlist (O1) ----------------
    async function initWishlistPage() {
        const grid = document.getElementById('wishlist-grid');
        const res = await api('/api/v1/wishlist', 'GET');
        grid.innerHTML = '';
        if (!res.success) {
            grid.appendChild(el('p', { class: 'empty-state' }, [res.error ? res.error.message : 'Sign in to view your wishlist.']));
            return;
        }
        if (res.data.length === 0) {
            grid.appendChild(el('p', { class: 'empty-state' }, ['Nothing saved yet — tap "Save for later" on any product.']));
            return;
        }
        res.data.forEach((item) => grid.appendChild(wishlistCard(item)));
    }

    function wishlistCard(item) {
        const thumb = el('div', { class: 'thumb' }, [el('img', { src: item.imageUrl || '', alt: item.productName })]);
        if (!item.inStock) thumb.appendChild(el('span', { class: 'badge-oos' }, ['Sold out']));
        return el('div', { class: 'product-card' }, [
            el('a', { href: 'product-detail.jsp?id=' + item.productId }, [
                thumb,
                el('p', { class: 'name' }, [item.productName]),
                el('p', { class: 'price' }, [money(item.price)])
            ]),
            el('button', {
                class: 'btn btn-sm', style: 'margin-top:12px;', type: 'button',
                onclick: async () => {
                    const r = await api('/api/v1/wishlist/' + item.id, 'DELETE');
                    if (r.success) initWishlistPage();
                }
            }, ['Remove'])
        ]);
    }

    function orderCard(order) {
        const itemsList = el('ul', { style: 'list-style:none;padding:0;margin:12px 0;' },
            order.items.map((i) => el('li', { style: 'padding:6px 0;font-size:13px;' }, [
                i.quantity + ' × ' + i.productName + ' — ' + money(i.unitPrice)
            ])));
        return el('div', { class: 'panel', style: 'margin-bottom:16px;' }, [
            el('div', { style: 'display:flex;justify-content:space-between;align-items:center;' }, [
                el('strong', {}, ['Order #' + order.id]),
                el('span', { class: 'status-pill ' + order.status }, [order.status])
            ]),
            itemsList,
            el('div', { class: 'summary-row total' }, [el('span', {}, ['Total']), el('span', {}, [money(order.totalAmount)])])
        ]);
    }

    // ---------------- Seller dashboard (F2, F6, O3) ----------------
    async function initSellerDashboard() {
        const [productsRes, ordersRes] = await Promise.all([
            api('/api/v1/seller/products', 'GET'),
            api('/api/v1/seller/orders', 'GET')
        ]);
        renderStats(productsRes, ordersRes);
        renderSellerListings(productsRes);
        renderSellerOrders(ordersRes);
        wireNewListingForm();
        const sellerChartSlot = document.getElementById('seller-sales-chart');
        if (sellerChartSlot) {
            const lines = ordersRes.success ? ordersRes.data : [];
            const buckets = aggregateByDay(lines, 14, (o) => o.unitPrice * o.quantity);
            renderSalesChart(sellerChartSlot, buckets, 'Revenue — last 14 days');
        }
    }

    function renderStats(productsRes, ordersRes) {
        const grid = document.getElementById('stat-grid');
        grid.innerHTML = '';
        const products = productsRes.success ? productsRes.data : [];
        const orders = ordersRes.success ? ordersRes.data : [];
        const revenue = orders.reduce((sum, o) => sum + (o.unitPrice * o.quantity), 0);
        const stats = [
            ['Active listings', products.filter((p) => p.active).length],
            ['Incoming order lines', orders.length],
            ['Gross revenue (all-time)', money(revenue)]
        ];
        stats.forEach(([label, value]) => {
            grid.appendChild(el('div', { class: 'stat-card' }, [
                el('p', { class: 'eyebrow' }, [label]),
                el('p', { class: 'value' }, [String(value)])
            ]));
        });
    }

    function renderSellerListings(res) {
        const container = document.getElementById('seller-listings');
        container.innerHTML = '';
        if (!res.success || res.data.length === 0) {
            container.appendChild(el('p', { class: 'empty-state' }, ['No listings yet — add your first piece.']));
            return;
        }
        const table = el('table', { class: 'data-table' }, [
            el('thead', {}, [el('tr', {}, ['Name', 'Price', 'Stock', 'Category', 'Status', 'Action'].map((h) => el('th', {}, [h])))]),
            el('tbody', {}, res.data.map(sellerListingRow))
        ]);
        container.appendChild(table);
    }

    // Restocking/repricing an existing listing (as opposed to publishing a new one) — inline
    // edit on the Listings row, backed by the same PUT /api/v1/products/{id} the "Add listing"
    // form's servlet already exposes. Deactivate/Reactivate reuses DELETE (soft-delete) and this
    // same PUT with active toggled, respectively.
    function sellerListingRow(p) {
        const priceCell = el('td', {}, [money(p.price)]);
        const stockCell = el('td', {}, [String(p.stockQty)]);
        const statusCell = el('td', {}, [p.active ? 'Active' : 'Removed']);
        const row = el('tr', {}, [
            el('td', {}, [p.name]), priceCell, stockCell, el('td', {}, [p.category]), statusCell
        ]);

        function renderViewMode() {
            priceCell.innerHTML = '';
            priceCell.appendChild(document.createTextNode(money(p.price)));
            if (isOnSale(p)) {
                priceCell.appendChild(el('div', { style: 'color:var(--muted);font-size:12px;text-decoration:line-through;' }, [money(p.compareAtPrice)]));
            }
            stockCell.innerHTML = '';
            stockCell.appendChild(document.createTextNode(String(p.stockQty)));
            actionCell.innerHTML = '';
            actionCell.appendChild(el('div', { style: 'display:flex;gap:8px;' }, [
                el('button', { class: 'btn btn-sm', onclick: renderEditMode }, ['Edit']),
                el('button', { class: 'btn btn-sm', onclick: toggleActive }, [p.active ? 'Deactivate' : 'Reactivate'])
            ]));
        }

        function renderEditMode() {
            const priceInput = el('input', { type: 'number', step: '0.01', min: '0', value: String(p.price), style: 'width:90px;' });
            const comparePriceInput = el('input', {
                type: 'number', step: '0.01', min: '0', value: p.compareAtPrice != null ? String(p.compareAtPrice) : '',
                placeholder: 'Was (sale)', style: 'width:90px;margin-top:4px;'
            });
            const stockInput = el('input', { type: 'number', min: '0', value: String(p.stockQty), style: 'width:70px;' });
            priceCell.innerHTML = '';
            priceCell.appendChild(el('div', {}, [priceInput, comparePriceInput]));
            stockCell.innerHTML = '';
            stockCell.appendChild(stockInput);
            actionCell.innerHTML = '';
            actionCell.appendChild(el('div', { style: 'display:flex;gap:8px;' }, [
                el('button', {
                    class: 'btn btn-sm btn-primary', onclick: async () => {
                        const body = Object.assign({}, p, {
                            price: parseFloat(priceInput.value),
                            compareAtPrice: comparePriceInput.value ? parseFloat(comparePriceInput.value) : null,
                            stockQty: parseInt(stockInput.value, 10)
                        });
                        const r = await api('/api/v1/products/' + p.id, 'PUT', body);
                        if (r.success) {
                            p.price = body.price;
                            p.compareAtPrice = body.compareAtPrice;
                            p.stockQty = body.stockQty;
                            toast('Listing updated');
                            renderViewMode();
                        } else {
                            toast(r.error ? r.error.message : 'Could not update listing');
                        }
                    }
                }, ['Save']),
                el('button', { class: 'btn btn-sm', onclick: renderViewMode }, ['Cancel'])
            ]));
        }

        async function toggleActive() {
            const r = p.active
                ? await api('/api/v1/products/' + p.id, 'DELETE')
                : await api('/api/v1/products/' + p.id, 'PUT', Object.assign({}, p, { active: true }));
            if (r.success) {
                p.active = !p.active;
                statusCell.innerHTML = '';
                statusCell.appendChild(document.createTextNode(p.active ? 'Active' : 'Removed'));
                toast(p.active ? 'Listing reactivated' : 'Listing deactivated');
                renderViewMode();
            } else {
                toast(r.error ? r.error.message : 'Could not update listing');
            }
        }

        const actionCell = el('td', {}, []);
        row.appendChild(actionCell);
        renderViewMode();
        return row;
    }

    function renderSellerOrders(res) {
        const container = document.getElementById('seller-orders');
        container.innerHTML = '';
        if (!res.success || res.data.length === 0) {
            container.appendChild(el('p', { class: 'empty-state' }, ['No incoming orders yet.']));
            return;
        }
        const table = el('table', { class: 'data-table' }, [
            el('thead', {}, [el('tr', {}, ['Order', 'Buyer', 'Product', 'Qty', 'Status', 'Action'].map((h) => el('th', {}, [h])))]),
            el('tbody', {}, res.data.map((o) => {
                const select = el('select', {}, nextStatuses(o.status).map((s) => el('option', { value: s }, [s])));
                const btn = el('button', {
                    class: 'btn btn-sm', onclick: async () => {
                        const r = await api('/api/v1/orders/' + o.orderId + '/status', 'PUT', { status: select.value });
                        if (r.success) { toast('Status updated'); initSellerDashboard(); }
                        else toast(r.error ? r.error.message : 'Could not update status');
                    }
                }, ['Update']);
                const actionCell = nextStatuses(o.status).length > 0 ? el('div', { style: 'display:flex;gap:8px;' }, [select, btn]) : el('span', {}, ['—']);
                return el('tr', {}, [
                    el('td', {}, ['#' + o.orderId]),
                    el('td', {}, [o.buyerName]),
                    el('td', {}, [o.productName]),
                    el('td', {}, [String(o.quantity)]),
                    el('td', {}, [el('span', { class: 'status-pill ' + o.status }, [o.status])]),
                    el('td', {}, [actionCell])
                ]);
            }))
        ]);
        container.appendChild(table);
    }

    function nextStatuses(current) {
        const map = { PENDING: ['CONFIRMED', 'CANCELLED'], CONFIRMED: ['SHIPPED', 'CANCELLED'], SHIPPED: ['DELIVERED'], DELIVERED: [], CANCELLED: [] };
        return map[current] || [];
    }

    function wireNewListingForm() {
        const form = document.getElementById('new-listing-form');
        if (!form || form.dataset.wired) return;
        form.dataset.wired = 'true';
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const errorEl = document.getElementById('listing-error');
            errorEl.textContent = '';
            const comparePriceRaw = document.getElementById('p-compare-price').value;
            const body = {
                name: document.getElementById('p-name').value.trim(),
                description: document.getElementById('p-desc').value.trim(),
                price: parseFloat(document.getElementById('p-price').value),
                compareAtPrice: comparePriceRaw ? parseFloat(comparePriceRaw) : null,
                stockQty: parseInt(document.getElementById('p-stock').value, 10),
                category: document.getElementById('p-category').value.trim(),
                imageUrl: document.getElementById('p-image').value.trim(),
                active: true
            };
            const res = await api('/api/v1/products', 'POST', body);
            if (res.success) {
                toast('Listing published');
                form.reset();
                initSellerDashboard();
                document.querySelector('.tab-btn[data-tab="listings"]').click();
            } else {
                errorEl.textContent = res.error ? res.error.message : 'Could not publish listing';
            }
        });
    }

    // ---------------- Admin (F7) ----------------
    async function initAdminPage() {
        const [usersRes, ordersRes, productsRes] = await Promise.all([
            api('/api/v1/admin/users', 'GET'),
            api('/api/v1/admin/orders', 'GET'),
            api('/api/v1/admin/products', 'GET')
        ]);
        renderAdminUsers(usersRes);
        renderAdminOrders(ordersRes);
        renderAdminProducts(productsRes);
        const adminChartSlot = document.getElementById('admin-sales-chart');
        if (adminChartSlot) {
            const orders = ordersRes.success ? ordersRes.data : [];
            const buckets = aggregateByDay(orders, 14, (o) => o.totalAmount);
            renderSalesChart(adminChartSlot, buckets, 'Marketplace revenue — last 14 days');
        }
    }

    function renderAdminUsers(res) {
        const container = document.getElementById('admin-users');
        container.innerHTML = '';
        if (!res.success) {
            container.appendChild(el('p', { class: 'empty-state' }, [res.error ? res.error.message : 'Admins only.']));
            return;
        }
        const table = el('table', { class: 'data-table' }, [
            el('thead', {}, [el('tr', {}, ['Name', 'Email', 'Role', 'Joined'].map((h) => el('th', {}, [h])))]),
            el('tbody', {}, res.data.map((u) => el('tr', {}, [
                el('td', {}, [u.name]),
                el('td', {}, [u.email]),
                el('td', {}, [u.role]),
                el('td', {}, [(u.createdAt || '').substring(0, 10)])
            ])))
        ]);
        container.appendChild(table);
    }

    function renderAdminOrders(res) {
        const container = document.getElementById('admin-orders');
        container.innerHTML = '';
        if (!res.success) {
            container.appendChild(el('p', { class: 'empty-state' }, [res.error ? res.error.message : 'Admins only.']));
            return;
        }
        if (res.data.length === 0) {
            container.appendChild(el('p', { class: 'empty-state' }, ['No orders placed yet.']));
            return;
        }
        res.data.forEach((order) => container.appendChild(orderCard(order)));
    }

    const LOW_STOCK_THRESHOLD = 5;

    function renderAdminProducts(res) {
        const container = document.getElementById('admin-products');
        container.innerHTML = '';
        if (!res.success) {
            container.appendChild(el('p', { class: 'empty-state' }, [res.error ? res.error.message : 'Admins only.']));
            return;
        }
        if (res.data.length === 0) {
            container.appendChild(el('p', { class: 'empty-state' }, ['No products listed yet.']));
            return;
        }
        const lowStockCount = res.data.filter((p) => p.active && p.stockQty <= LOW_STOCK_THRESHOLD).length;
        if (lowStockCount > 0) {
            container.appendChild(el('p', { class: 'form-error', style: 'margin-bottom:16px;' },
                [lowStockCount + ' listing(s) at ' + LOW_STOCK_THRESHOLD + ' units or fewer — restock is the seller’s action, from their own dashboard.']));
        }
        const table = el('table', { class: 'data-table' }, [
            el('thead', {}, [el('tr', {}, ['Product', 'Seller', 'Price', 'Stock', 'Category', 'Status', 'Action'].map((h) => el('th', {}, [h])))]),
            el('tbody', {}, res.data.map((p) => {
                const lowStock = p.active && p.stockQty <= LOW_STOCK_THRESHOLD;
                const removeBtn = p.active ? el('button', {
                    class: 'btn btn-sm', onclick: async () => {
                        if (!confirm('Remove "' + p.name + '" from the marketplace?')) return;
                        const r = await api('/api/v1/admin/products/' + p.id, 'DELETE');
                        if (r.success) { toast('Listing removed'); initAdminPage(); }
                        else toast(r.error ? r.error.message : 'Could not remove listing');
                    }
                }, ['Remove']) : el('span', {}, ['—']);
                return el('tr', {}, [
                    el('td', {}, [p.name]),
                    el('td', {}, [p.sellerName || ('#' + p.sellerId)]),
                    el('td', {}, [money(p.price)]),
                    el('td', { style: lowStock ? 'color:var(--danger);font-weight:600;' : '' }, [String(p.stockQty)]),
                    el('td', {}, [p.category]),
                    el('td', {}, [p.active ? 'Active' : 'Removed']),
                    el('td', {}, [removeBtn])
                ]);
            }))
        ]);
        container.appendChild(table);
    }
})(window);
