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
        return '₹' + n.toFixed(2);
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

    function productCard(p) {
        const thumb = el('div', { class: 'thumb' }, [
            el('img', { src: p.imageUrl || '', alt: p.name, loading: 'lazy' })
        ]);
        if (p.stockQty <= 0) {
            thumb.appendChild(el('span', { class: 'badge-oos' }, ['Sold out']));
        }
        return el('a', { class: 'product-card', href: 'product-detail.jsp?id=' + p.id }, [
            thumb,
            el('p', { class: 'category' }, [p.category]),
            el('p', { class: 'name' }, [p.name]),
            el('p', { class: 'price' }, [money(p.price)]),
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

        const qtyState = { value: 1 };
        const qtyLabel = el('span', {}, [String(qtyState.value)]);
        const addBtn = el('button', {
            class: 'btn btn-primary btn-block', style: 'margin-top:24px;',
            onclick: async () => {
                const r = await api('/api/v1/cart', 'POST', { productId: p.id, quantity: qtyState.value });
                if (r.success) { toast('Added to bag'); updateCartBadge(); }
                else toast(r.error ? r.error.message : 'Could not add to bag');
            }
        }, [p.stockQty > 0 ? 'Add to bag' : 'Notify me']);
        if (p.stockQty <= 0) addBtn.disabled = true;

        root.appendChild(el('div', { class: 'pd-image' }, [el('img', { src: p.imageUrl || '', alt: p.name })]));
        root.appendChild(el('div', {}, [
            el('p', { class: 'category' }, [p.category]),
            el('h1', { class: 'display', style: 'font-size:36px;margin:12px 0;' }, [p.name]),
            p.reviewCount > 0 ? el('p', { class: 'rating' }, [stars(p.averageRating) + ' · ' + p.reviewCount + ' review(s)']) : null,
            el('p', { class: 'pd-price' }, [money(p.price)]),
            el('p', { style: 'color:var(--muted);line-height:1.7;' }, [p.description || '']),
            el('div', { class: 'qty-control', style: 'margin-top:24px;' }, [
                el('button', { type: 'button', onclick: () => { if (qtyState.value > 1) { qtyState.value--; qtyLabel.textContent = qtyState.value; } } }, ['−']),
                qtyLabel,
                el('button', { type: 'button', onclick: () => { qtyState.value++; qtyLabel.textContent = qtyState.value; } }, ['+'])
            ]),
            addBtn
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

    document.addEventListener('DOMContentLoaded', () => {
        wireLogout();
        wireTabs();
        updateCartBadge();
    });

    global.AdharshMart = {
        api, el, money, stars, toast, productCard, renderFeatured,
        initProductsPage, initProductDetailPage, updateCartBadge,
        initCartPage: () => global.AdharshMartCart && global.AdharshMartCart.initCartPage(),
        initCheckoutPage: () => global.AdharshMartCart && global.AdharshMartCart.initCheckoutPage(),
        initOrdersPage,
        initSellerDashboard,
        initAdminPage
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
            el('thead', {}, [el('tr', {}, ['Name', 'Price', 'Stock', 'Category', 'Status'].map((h) => el('th', {}, [h])))]),
            el('tbody', {}, res.data.map((p) => el('tr', {}, [
                el('td', {}, [p.name]),
                el('td', {}, [money(p.price)]),
                el('td', {}, [String(p.stockQty)]),
                el('td', {}, [p.category]),
                el('td', {}, [p.active ? 'Active' : 'Removed'])
            ])))
        ]);
        container.appendChild(table);
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
            const body = {
                name: document.getElementById('p-name').value.trim(),
                description: document.getElementById('p-desc').value.trim(),
                price: parseFloat(document.getElementById('p-price').value),
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
        const [usersRes, ordersRes] = await Promise.all([
            api('/api/v1/admin/users', 'GET'),
            api('/api/v1/admin/orders', 'GET')
        ]);
        renderAdminUsers(usersRes);
        renderAdminOrders(ordersRes);
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
})(window);
