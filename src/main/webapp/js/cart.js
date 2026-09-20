/** Cart (F4) and checkout (F5) page logic — depends on AdharshMart.api/el/money from app.js. */
(function (global) {
    const { api, el, money, toast } = global.AdharshMart;

    async function initCartPage() {
        const linesEl = document.getElementById('cart-lines');
        const summaryEl = document.getElementById('cart-summary');
        const checkoutBtn = document.getElementById('checkout-btn');

        async function load() {
            const res = await api('/api/v1/cart', 'GET');
            linesEl.innerHTML = '';
            summaryEl.innerHTML = '';
            if (!res.success) {
                linesEl.appendChild(el('p', { class: 'empty-state' }, [res.error ? res.error.message : 'Sign in to view your bag.']));
                checkoutBtn.disabled = true;
                return;
            }
            const items = res.data.items;
            if (items.length === 0) {
                linesEl.appendChild(el('p', { class: 'empty-state' }, ['Your bag is empty.']));
                checkoutBtn.disabled = true;
                return;
            }
            items.forEach((item) => linesEl.appendChild(cartLine(item, load)));
            summaryEl.appendChild(el('div', { class: 'summary-row' }, [el('span', {}, ['Subtotal']), el('span', {}, [money(res.data.total)])]));
            summaryEl.appendChild(el('div', { class: 'summary-row' }, [el('span', {}, ['Shipping']), el('span', {}, ['Complimentary'])]));
            summaryEl.appendChild(el('div', { class: 'summary-row total' }, [el('span', {}, ['Total']), el('span', {}, [money(res.data.total)])]));
            checkoutBtn.disabled = false;
        }

        checkoutBtn.addEventListener('click', () => {
            window.location.href = 'checkout.jsp';
        });

        load();
    }

    function cartLine(item, onChange) {
        const qtyLabel = el('span', {}, [String(item.quantity)]);
        const update = async (nextQty) => {
            if (nextQty < 1) return;
            const res = await api('/api/v1/cart/' + item.id, 'PUT', { quantity: nextQty });
            if (res.success) onChange();
        };
        return el('div', { class: 'cart-line' }, [
            el('img', { src: item.imageUrl || '', alt: item.productName }),
            el('div', {}, [
                el('p', { class: 'name', style: 'font-size:15px;margin-bottom:4px;' }, [item.productName]),
                el('p', { style: 'color:var(--muted);font-size:13px;margin-bottom:10px;' }, [money(item.unitPrice) + ' each']),
                el('div', { class: 'qty-control' }, [
                    el('button', { type: 'button', onclick: () => update(item.quantity - 1) }, ['−']),
                    qtyLabel,
                    el('button', { type: 'button', onclick: () => update(item.quantity + 1) }, ['+'])
                ])
            ]),
            el('div', { style: 'text-align:right;' }, [
                el('p', { style: 'font-weight:600;margin-bottom:12px;' }, [money(item.lineTotal)]),
                el('button', {
                    class: 'btn btn-sm', type: 'button',
                    onclick: async () => { const r = await api('/api/v1/cart/' + item.id, 'DELETE'); if (r.success) onChange(); }
                }, ['Remove'])
            ])
        ]);
    }

    async function initCheckoutPage() {
        const linesEl = document.getElementById('checkout-lines');
        const summaryEl = document.getElementById('checkout-summary');
        const confirmBtn = document.getElementById('confirm-btn');
        const errorEl = document.getElementById('checkout-error');

        const res = await api('/api/v1/cart', 'GET');
        if (!res.success || res.data.items.length === 0) {
            linesEl.appendChild(el('p', { class: 'empty-state' }, ['Your bag is empty.']));
            confirmBtn.disabled = true;
            return;
        }
        res.data.items.forEach((item) => {
            linesEl.appendChild(el('div', { style: 'display:flex;justify-content:space-between;padding:8px 0;font-size:14px;' }, [
                el('span', {}, [item.quantity + ' × ' + item.productName]),
                el('span', {}, [money(item.lineTotal)])
            ]));
        });
        summaryEl.appendChild(el('div', { class: 'summary-row total' }, [el('span', {}, ['Total']), el('span', {}, [money(res.data.total)])]));

        confirmBtn.addEventListener('click', async () => {
            confirmBtn.disabled = true;
            confirmBtn.textContent = 'Confirming payment…';
            const orderRes = await api('/api/v1/orders/checkout', 'POST', {});
            if (orderRes.success) {
                toast('Order placed');
                window.location.href = 'orders.jsp';
            } else {
                errorEl.textContent = orderRes.error ? orderRes.error.message : 'Checkout failed';
                confirmBtn.disabled = false;
                confirmBtn.textContent = 'Confirm mock payment & place order';
            }
        });
    }

    global.AdharshMartCart = { initCartPage, initCheckoutPage };
})(window);
