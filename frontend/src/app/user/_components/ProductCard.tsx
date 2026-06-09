'use client';

import { useState } from 'react';
import type { Product } from '../_types';

const FALLBACK_SRC =
  "data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='400' height='300'%3E%3Crect width='400' height='300' fill='%23f3f4f6'/%3E%3Cellipse cx='200' cy='145' rx='58' ry='40' fill='%23d1d5db'/%3E%3Cpath d='M200 105 C178 122 178 168 200 185' stroke='%239ca3af' stroke-width='3' fill='none' stroke-linecap='round'/%3E%3Cpath d='M200 105 C222 122 222 168 200 185' stroke='%23d1d5db' stroke-width='1.5' fill='none' stroke-linecap='round'/%3E%3Ctext x='200' y='225' text-anchor='middle' font-family='sans-serif' font-size='13' fill='%239ca3af'%3E이미지를 불러올 수 없습니다%3C/text%3E%3C/svg%3E";

function ImageWithFallback({ src, alt, className }: { src?: string; alt: string; className?: string }) {
  const [errored, setErrored] = useState(false);
  return (
    <img
      src={!src || errored ? FALLBACK_SRC : src}
      alt={alt}
      className={className}
      onError={() => setErrored(true)}
    />
  );
}

export function ProductCard({
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
        <ImageWithFallback src={product.imageUrl} alt={product.name} className="w-full h-full object-cover" />
      </div>
      <div className="p-5">
        <h3 className="font-bold text-gray-900 text-lg leading-tight">{product.name}</h3>
        <p className="text-sm text-gray-500 mt-1.5 leading-relaxed min-h-[40px]">{product.description}</p>
        <p className="text-gray-900 font-bold text-2xl mt-3">
          {product.price.toLocaleString()}
          <span className="text-base font-semibold ml-0.5">원</span>
        </p>
        <div className="flex items-center gap-3 mt-4">
          <div className="flex items-center border border-gray-200 rounded-xl overflow-hidden">
            <button onClick={onDecrease} className="w-9 h-9 flex items-center justify-center text-gray-700 hover:bg-gray-100 transition-colors font-bold text-lg">−</button>
            <span className="w-9 text-center text-gray-900 font-semibold text-sm select-none">{quantity}</span>
            <button onClick={onIncrease} className="w-9 h-9 flex items-center justify-center text-gray-700 hover:bg-gray-100 transition-colors font-bold text-lg">+</button>
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
