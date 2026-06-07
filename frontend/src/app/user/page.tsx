'use client';

import { useState, useEffect } from 'react';

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl?: string;
}

interface CartItem {
  product: Product;
  quantity: number;
}

const FALLBACK_SRC =
  "data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='300'%3E%3Crect width='400' height='300' fill='%23f3f4f6'/%3E%3Cellipse cx='200' cy='145' rx='58' ry='40' fill='%23d1d5db'/%3E%3Cpath d='M200 105 C178 122 178 168 200 185' stroke='%239ca3af' stroke-width='3' fill='none' stroke-linecap='round'/%3E%3Cpath d='M200 105 C222 122 222 168 200 185' stroke='%23d1d5db' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3Ctext x='200' y='225' text-anchor='middle' font-family='sans-serif' font-size='13' fill='%239ca3af'%3E이미지를 불러올 수 없습니다%3C/text%3E%3C/svg%3E";

const MOCK_PRODUCTS: Product[] = [
  {
    id: 1,
    name: 'Colombia Narino',
    description: '달콤한 카라멜과 견과류의 부드러운 맛. 균형 잡힌 미디엄 로스트.',
    price: 12000,
    category: 'COFFEE_BEAN_PACKAGE',
    imageUrl:
      'https://images.unsplash.com/photo-1698093135407-8f50f3a52fe2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
  },
  {
    id: 2,
    name: 'Ethiopia Yirgacheffe',
    description: '꽃향기와 베리의 과일향이 풍부한 밝은 산미. 라이트 로스트.',
    price: 15000,
    category: 'COFFEE_BEAN_PACKAGE',
    imageUrl:
      'https://images.unsplash.com/photo-1666873903780-396269c73a54?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
  },
  {
    id: 3,
    name: 'Guatemala Antigua',
    description: '다크 초콜릿과 스파이시한 향의 미디엄-다크 로스트.',
    price: 13000,
    category: 'COFFEE_BEAN_PACKAGE',
    imageUrl:
      'https://images.unsplash.com/photo-1765896977022-3079fd6bce8d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
  },
  {
    id: 4,
    name: 'Sumatra Mandheling',
    description: '흙내음과 깊은 풀바디, 스모키한 다크 로스트.',
    price: 14000,
    category: 'COFFEE_BEAN_PACKAGE',
    imageUrl:
      'https://images.unsplash.com/photo-1666873975263-0c0e24c1a2f4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
  },
];

function ImageWithFallback({
  src,
  alt,
  className,
}: {
  src?: string;
  alt: string;
  className?: string;
}) {
  const [errored, setErrored] = useState(false);
  const imgSrc = !src || errored ? FALLBACK_SRC : src;

  return (
    <img
      src={imgSrc}
      alt={alt}
      className={className}
      onError={() => setErrored(true)}
    />
  );
}

function ProductCard({
  product,
  quantity,
  onIncrease,
  onDecrease,
  onAddToCart,
}: {
  product: Product;
  quantity: number;
  onIncrease: () => void;
  onDecrease: () => void;
  onAddToCart: () => void;
}) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow duration-200">
      <div className="aspect-[4/3] overflow-hidden bg-gray-50">
        <ImageWithFallback
          src={product.imageUrl}
          alt={product.name}
          className="w-full h-full object-cover"
        />
      </div>

      <div className="p-5">
        <h3 className="font-bold text-gray-900 text-lg leading-tight">{product.name}</h3>
        <p className="text-sm text-gray-500 mt-1.5 leading-relaxed min-h-[40px]">
          {product.description}
        </p>
        <p className="text-gray-900 font-bold text-2xl mt-3">
          {product.price.toLocaleString()}
          <span className="text-base font-semibold ml-0.5">원</span>
        </p>

        <div className="flex items-center gap-3 mt-4">
          <div className="flex items-center border border-gray-200 rounded-xl overflow-hidden">
            <button
              onClick={onDecrease}
              className="w-9 h-9 flex items-center justify-center text-gray-700 hover:bg-gray-100 transition-colors font-bold text-lg"
            >
              −
            </button>
            <span className="w-9 text-center text-gray-900 font-semibold text-sm select-none">
              {quantity}
            </span>
            <button
              onClick={onIncrease}
              className="w-9 h-9 flex items-center justify-center text-gray-700 hover:bg-gray-100 transition-colors font-bold text-lg"
            >
              +
            </button>
          </div>

          <button
            onClick={onAddToCart}
            className="flex-1 bg-black text-white py-2.5 rounded-xl font-semibold text-sm hover:bg-gray-800 active:bg-gray-900 transition-colors"
          >
            장바구니 담기
          </button>
        </div>
      </div>
    </div>
  );
}

function CartSection({
  items,
  total,
  email,
  postcode,
  isOrdering,
  orderResult,
  canOrder,
  onChangeCartQuantity,
  onRemoveFromCart,
  onEmailChange,
  onPostcodeChange,
  onOrder,
  onDismissResult,
}: {
  items: CartItem[];
  total: number;
  email: string;
  postcode: string;
  isOrdering: boolean;
  orderResult: 'success' | 'error' | null;
  canOrder: boolean;
  onChangeCartQuantity: (productId: number, delta: number) => void;
  onRemoveFromCart: (productId: number) => void;
  onEmailChange: (v: string) => void;
  onPostcodeChange: (v: string) => void;
  onOrder: () => void;
  onDismissResult: () => void;
}) {
  const totalCount = items.reduce((s, i) => s + i.quantity, 0);

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
      {/* Header */}
      <div className="bg-black px-5 py-4 flex items-center gap-2.5">
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="white"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <circle cx="9" cy="21" r="1" />
          <circle cx="20" cy="21" r="1" />
          <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" />
        </svg>
        <h2 className="font-bold text-white text-lg">장바구니</h2>
        {totalCount > 0 && (
          <span className="ml-auto bg-white text-black text-xs font-bold w-5 h-5 rounded-full flex items-center justify-center">
            {totalCount}
          </span>
        )}
      </div>

      <div className="p-5 flex flex-col gap-4">
        {/* Cart items */}
        {items.length === 0 ? (
          <div className="py-8 text-center">
            <p className="text-3xl mb-2">☕</p>
            <p className="text-sm text-gray-400">담은 상품이 없습니다</p>
          </div>
        ) : (
          <div className="space-y-3">
            {items.map((item) => (
              <div
                key={item.product.id}
                className="flex items-center gap-3 pb-3 border-b border-gray-100 last:border-0 last:pb-0"
              >
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-gray-900 text-sm truncate">
                    {item.product.name}
                  </p>
                  <p className="text-xs text-gray-500 mt-0.5 font-medium">
                    {(item.product.price * item.quantity).toLocaleString()}원
                  </p>
                </div>

                <div className="flex items-center border border-gray-200 rounded-lg overflow-hidden shrink-0">
                  <button
                    onClick={() => onChangeCartQuantity(item.product.id, -1)}
                    className="w-7 h-7 flex items-center justify-center text-gray-600 hover:bg-gray-100 transition-colors font-bold text-base"
                  >
                    −
                  </button>
                  <span className="w-7 text-center text-gray-900 text-sm font-semibold select-none">
                    {item.quantity}
                  </span>
                  <button
                    onClick={() => onChangeCartQuantity(item.product.id, 1)}
                    className="w-7 h-7 flex items-center justify-center text-gray-600 hover:bg-gray-100 transition-colors font-bold text-base"
                  >
                    +
                  </button>
                </div>

                <button
                  onClick={() => onRemoveFromCart(item.product.id)}
                  className="text-gray-300 hover:text-red-500 transition-colors text-xl leading-none shrink-0"
                  aria-label="삭제"
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        )}

        {/* Total */}
        <div className="flex items-center justify-between py-3 border-t border-b border-gray-100">
          <span className="font-semibold text-gray-900">합계</span>
          <span className="font-bold text-xl text-gray-900">
            {total.toLocaleString()}
            <span className="text-base font-semibold ml-0.5">원</span>
          </span>
        </div>

        {/* Delivery notice */}
        <div className="bg-gray-50 border border-gray-200 rounded-xl px-4 py-3 flex gap-2 items-start">
          <span className="text-sm mt-0.5">⏰</span>
          <p className="text-xs text-gray-600 leading-relaxed font-medium">
            오후 2시 이후 주문은 다음 날 배송이 시작됩니다.
          </p>
        </div>

        {/* Order form */}
        <div className="space-y-3">
          <div>
            <label className="block text-sm font-semibold text-gray-900 mb-1.5">
              이메일 <span className="text-red-400">*</span>
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => onEmailChange(e.target.value)}
              placeholder="example@email.com"
              className="w-full border border-gray-200 rounded-xl px-3.5 py-2.5 text-sm text-gray-900 placeholder-gray-300 focus:outline-none focus:border-black focus:ring-2 focus:ring-black/10 transition-all"
            />
          </div>
          <div>
            <label className="block text-sm font-semibold text-gray-900 mb-1.5">
              우편번호 <span className="text-red-400">*</span>
            </label>
            <input
              type="text"
              value={postcode}
              onChange={(e) => onPostcodeChange(e.target.value)}
              placeholder="12345"
              maxLength={6}
              className="w-full border border-gray-200 rounded-xl px-3.5 py-2.5 text-sm text-gray-900 placeholder-gray-300 focus:outline-none focus:border-black focus:ring-2 focus:ring-black/10 transition-all"
            />
          </div>
        </div>

        {/* Order result feedback */}
        {orderResult === 'success' && (
          <div className="bg-green-50 border border-green-200 rounded-xl px-4 py-3 flex items-start justify-between gap-2">
            <p className="text-sm text-green-700 font-medium">✓ 주문이 완료되었습니다!</p>
            <button
              onClick={onDismissResult}
              className="text-green-400 hover:text-green-600 text-xl leading-none shrink-0"
            >
              ×
            </button>
          </div>
        )}
        {orderResult === 'error' && (
          <div className="bg-red-50 border border-red-200 rounded-xl px-4 py-3 flex items-start justify-between gap-2">
            <p className="text-sm text-red-700 font-medium">
              주문에 실패했습니다. 다시 시도해주세요.
            </p>
            <button
              onClick={onDismissResult}
              className="text-red-400 hover:text-red-600 text-xl leading-none shrink-0"
            >
              ×
            </button>
          </div>
        )}

        {/* Order button */}
        <button
          onClick={onOrder}
          disabled={!canOrder || isOrdering}
          className="w-full bg-black text-white py-3.5 rounded-xl font-bold text-base hover:bg-gray-800 active:bg-gray-900 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
        >
          {isOrdering ? '주문 처리 중...' : '주문하기'}
        </button>

        {items.length > 0 && !canOrder && !isOrdering && (
          <p className="text-xs text-center text-gray-400">
            이메일과 우편번호를 입력하면 주문할 수 있습니다.
          </p>
        )}
      </div>
    </div>
  );
}

export default function ShopPage() {
  const [products, setProducts] = useState<Product[]>(MOCK_PRODUCTS);
  const [quantities, setQuantities] = useState<Record<number, number>>({});
  const [cart, setCart] = useState<CartItem[]>([]);
  const [email, setEmail] = useState('');
  const [postcode, setPostcode] = useState('');
  const [isOrdering, setIsOrdering] = useState(false);
  const [orderResult, setOrderResult] = useState<'success' | 'error' | null>(null);

  useEffect(() => {
    fetch('/api/v1/products')
      .then((res) => (res.ok ? res.json() : null))
      .then((data) => {
        if (Array.isArray(data) && data.length > 0) setProducts(data);
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
        return prev.map((i) =>
          i.product.id === product.id ? { ...i, quantity: i.quantity + qty } : i
        );
      }
      return [...prev, { product, quantity: qty }];
    });
    setQuantities((prev) => ({ ...prev, [product.id]: 1 }));
  };

  const changeCartQty = (id: number, delta: number) => {
    setCart((prev) =>
      prev
        .map((i) => (i.product.id === id ? { ...i, quantity: i.quantity + delta } : i))
        .filter((i) => i.quantity > 0)
    );
  };

  const removeFromCart = (id: number) => {
    setCart((prev) => prev.filter((i) => i.product.id !== id));
  };

  const total = cart.reduce((sum, i) => sum + i.product.price * i.quantity, 0);
  const canOrder = cart.length > 0 && email.trim() !== '' && postcode.trim() !== '';

  const handleOrder = async () => {
    if (!canOrder) return;
    setIsOrdering(true);
    setOrderResult(null);
    try {
      const res = await fetch('/api/v1/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email,
          address: postcode,
          postcode,
          orderItems: cart.map((i) => ({ productId: i.product.id, quantity: i.quantity })),
        }),
      });
      if (res.ok) {
        setOrderResult('success');
        setCart([]);
        setEmail('');
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
      {/* Header */}
      <header className="bg-black text-white shadow-lg sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center gap-4">
          <div className="text-3xl">☕</div>
          <div>
            <h1 className="text-xl font-bold tracking-wide">Grids &amp; Circles</h1>
            <p className="text-xs text-gray-400 font-medium mt-0.5 tracking-widest uppercase">
              Premium Coffee Beans
            </p>
          </div>
        </div>
      </header>

      {/* Main layout */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 py-8">
        <div className="flex flex-col lg:flex-row gap-8 items-start">
          {/* Left: Product list (scrollable) */}
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

          {/* Right: Cart (sticky) */}
          <aside className="w-full lg:w-96 shrink-0 lg:sticky lg:top-20">
            <CartSection
              items={cart}
              total={total}
              email={email}
              postcode={postcode}
              isOrdering={isOrdering}
              orderResult={orderResult}
              canOrder={canOrder}
              onChangeCartQuantity={changeCartQty}
              onRemoveFromCart={removeFromCart}
              onEmailChange={setEmail}
              onPostcodeChange={setPostcode}
              onOrder={handleOrder}
              onDismissResult={() => setOrderResult(null)}
            />
          </aside>
        </div>
      </main>
    </div>
  );
}
