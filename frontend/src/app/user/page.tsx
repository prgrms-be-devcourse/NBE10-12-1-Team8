'use client';

import { useState, useEffect } from 'react';
import type { Product, CartItem } from './_types';
import { MOCK_PRODUCTS } from './_mocks';
import { ProductCard } from './_components/ProductCard';
import { CartSection } from './_components/CartSection';
import { MyOrderDrawer } from './_components/MyOrderDrawer';

export default function ShopPage() {
  const [products, setProducts] = useState<Product[]>(MOCK_PRODUCTS);
  const [quantities, setQuantities] = useState<Record<number, number>>({});
  const [cart, setCart] = useState<CartItem[]>([]);
  const [email, setEmail] = useState('');
  const [address, setAddress] = useState('');
  const [postcode, setPostcode] = useState('');
  const [isOrdering, setIsOrdering] = useState(false);
  const [orderResult, setOrderResult] = useState<'success' | 'error' | null>(null);
  const [drawerOpen, setDrawerOpen] = useState(false);

  useEffect(() => {
    fetch('/api/products')
      .then((res) => (res.ok ? res.json() : null))
      .then((data) => {
        const list = data?.data;
        if (Array.isArray(list) && list.length > 0) setProducts(list);
      })
      .catch(() => {});
  }, []);

  const getQty = (id: number) => quantities[id] ?? 1;

  const changeProductQty = (id: number, delta: number) => {
    setQuantities((prev) => ({ ...prev, [id]: Math.max(1, (prev[id] ?? 1) + delta) }));
  };

  const addToCart = (product: Product) => {
    const qty = getQty(product.id);
    setCart((prev) => {
      const existing = prev.find((i) => i.product.id === product.id);
      if (existing) {
        return prev.map((i) => i.product.id === product.id ? { ...i, quantity: i.quantity + qty } : i);
      }
      return [...prev, { product, quantity: qty }];
    });
    setQuantities((prev) => ({ ...prev, [product.id]: 1 }));
  };

  const changeCartQty = (id: number, delta: number) => {
    setCart((prev) =>
      prev
        .map((i) => i.product.id === id ? { ...i, quantity: i.quantity + delta } : i)
        .filter((i) => i.quantity > 0),
    );
  };

  const removeFromCart = (id: number) => {
    setCart((prev) => prev.filter((i) => i.product.id !== id));
  };

  const total = cart.reduce((sum, i) => sum + i.product.price * i.quantity, 0);
  const canOrder = cart.length > 0 && email.trim() !== '' && address.trim() !== '' && postcode.trim() !== '';

  const handleOrder = async () => {
    if (!canOrder) return;
    setIsOrdering(true);
    setOrderResult(null);
    try {
      const res = await fetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email,
          address,
          zipcode: postcode,
          items: cart.map((i) => ({ productId: i.product.id, quantity: i.quantity })),
        }),
      });
      if (res.ok) {
        setOrderResult('success');
        setCart([]);
        setEmail('');
        setAddress('');
        setPostcode('');
      } else {
        setOrderResult('error');
      }
    } catch {
      setOrderResult('error');
    } finally {
      setIsOrdering(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-black text-white shadow-lg sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center gap-4">
          <div className="text-3xl">☕</div>
          <div>
            <h1 className="text-xl font-bold tracking-wide">Grids &amp; Circles</h1>
            <p className="text-xs text-gray-400 font-medium mt-0.5 tracking-widest uppercase">Premium Coffee Beans</p>
          </div>
          <button
            onClick={() => setDrawerOpen(true)}
            className="ml-auto flex items-center gap-2 text-gray-300 hover:text-white transition-colors group"
            aria-label="주문 목록 조회"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" />
            </svg>
            <span className="text-sm font-medium hidden sm:inline">주문 목록 조회</span>
          </button>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
        <div className="flex flex-col lg:flex-row gap-8 items-start">
          <section className="flex-1 min-w-0">
            <h2 className="text-xl font-bold text-gray-900 mb-6">원두 상품</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              {products.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  quantity={getQty(product.id)}
                  onIncrease={() => changeProductQty(product.id, 1)}
                  onDecrease={() => changeProductQty(product.id, -1)}
                  onAddToCart={() => addToCart(product)}
                />
              ))}
            </div>
          </section>

          <aside className="w-full lg:w-96 shrink-0 lg:sticky lg:top-20">
            <CartSection
              items={cart}
              total={total}
              email={email}
              address={address}
              postcode={postcode}
              isOrdering={isOrdering}
              orderResult={orderResult}
              canOrder={canOrder}
              onChangeCartQuantity={changeCartQty}
              onRemoveFromCart={removeFromCart}
              onEmailChange={setEmail}
              onAddressChange={setAddress}
              onPostcodeChange={setPostcode}
              onOrder={handleOrder}
              onDismissResult={() => setOrderResult(null)}
            />
          </aside>
        </div>
      </main>

      {drawerOpen && <MyOrderDrawer onClose={() => setDrawerOpen(false)} />}
    </div>
  );
}
