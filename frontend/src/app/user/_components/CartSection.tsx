import type { CartItem } from '../_types';

export function CartSection({
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
      <div className="bg-black px-5 py-4 flex items-center gap-2.5">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="9" cy="21" r="1" /><circle cx="20" cy="21" r="1" />
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
        {items.length === 0 ? (
          <div className="py-8 text-center">
            <p className="text-3xl mb-2">☕</p>
            <p className="text-sm text-gray-400">담은 상품이 없습니다</p>
          </div>
        ) : (
          <div className="space-y-3">
            {items.map((item) => (
              <div key={item.product.id} className="flex items-center gap-3 pb-3 border-b border-gray-100 last:border-0 last:pb-0">
                <div className="flex-1 min-w-0">
                  <p className="font-semibold text-gray-900 text-sm truncate">{item.product.name}</p>
                  <p className="text-xs text-gray-500 mt-0.5 font-medium">
                    {(item.product.price * item.quantity).toLocaleString()}원
                  </p>
                </div>
                <div className="flex items-center border border-gray-200 rounded-lg overflow-hidden shrink-0">
                  <button onClick={() => onChangeCartQuantity(item.product.id, -1)} className="w-7 h-7 flex items-center justify-center text-gray-600 hover:bg-gray-100 transition-colors font-bold text-base">−</button>
                  <span className="w-7 text-center text-gray-900 text-sm font-semibold select-none">{item.quantity}</span>
                  <button onClick={() => onChangeCartQuantity(item.product.id, 1)} className="w-7 h-7 flex items-center justify-center text-gray-600 hover:bg-gray-100 transition-colors font-bold text-base">+</button>
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

        <div className="flex items-center justify-between py-3 border-t border-b border-gray-100">
          <span className="font-semibold text-gray-900">합계</span>
          <span className="font-bold text-xl text-gray-900">
            {total.toLocaleString()}
            <span className="text-base font-semibold ml-0.5">원</span>
          </span>
        </div>

        <div className="bg-gray-50 border border-gray-200 rounded-xl px-4 py-3 flex gap-2 items-start">
          <span className="text-sm mt-0.5">⏰</span>
          <p className="text-xs text-gray-600 leading-relaxed font-medium">오후 2시 이후 주문은 다음 날 배송이 시작됩니다.</p>
        </div>

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

        {orderResult === 'success' && (
          <div className="bg-green-50 border border-green-200 rounded-xl px-4 py-3 flex items-start justify-between gap-2">
            <p className="text-sm text-green-700 font-medium">✓ 주문이 완료되었습니다!</p>
            <button onClick={onDismissResult} className="text-green-400 hover:text-green-600 text-xl leading-none shrink-0">×</button>
          </div>
        )}
        {orderResult === 'error' && (
          <div className="bg-red-50 border border-red-200 rounded-xl px-4 py-3 flex items-start justify-between gap-2">
            <p className="text-sm text-red-700 font-medium">주문에 실패했습니다. 다시 시도해주세요.</p>
            <button onClick={onDismissResult} className="text-red-400 hover:text-red-600 text-xl leading-none shrink-0">×</button>
          </div>
        )}

        <button
          onClick={onOrder}
          disabled={!canOrder || isOrdering}
          className="w-full bg-black text-white py-3.5 rounded-xl font-bold text-base hover:bg-gray-800 active:bg-gray-900 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
        >
          {isOrdering ? '주문 처리 중...' : '주문하기'}
        </button>
        {items.length > 0 && !canOrder && !isOrdering && (
          <p className="text-xs text-center text-gray-400">이메일과 우편번호를 입력하면 주문할 수 있습니다.</p>
        )}
      </div>
    </div>
  );
}
