'use client';

import { useState } from 'react';
import type { Order } from '../_types';

export function EditAddressModal({
  order,
  onClose,
  onSave,
  isSaving,
}: {
  order: Order;
  onClose: () => void;
  onSave: (address: string, zipcode: string) => void;
  isSaving: boolean;
}) {
  const [address, setAddress] = useState(order.address);
  const [zipcode, setZipcode] = useState(order.zipcode);
  const canSave = address.trim() !== '' && zipcode.trim() !== '' && !isSaving;

  return (
    <div
      className="fixed inset-0 z-[200] flex items-center justify-center bg-black/50 px-4"
      onMouseDown={onClose}
    >
      <div
        className="w-full max-w-md rounded-2xl bg-white shadow-2xl"
        onMouseDown={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between border-b border-gray-100 px-6 py-5">
          <h3 className="text-lg font-bold text-gray-900">배송지 수정</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 text-2xl leading-none">×</button>
        </div>

        <div className="px-6 py-5 space-y-4">
          <p className="text-sm text-gray-500">
            주문 <span className="font-semibold text-gray-800">#{order.id}</span>의 배송지를 변경합니다.
          </p>
          <div>
            <label className="block text-sm font-semibold text-gray-900 mb-1.5">
              주소 <span className="text-red-400">*</span>
            </label>
            <input
              type="text"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              placeholder="배송 받을 주소를 입력하세요"
              className="w-full border border-gray-200 rounded-xl px-3.5 py-2.5 text-sm text-gray-900 placeholder-gray-300 focus:outline-none focus:border-black focus:ring-2 focus:ring-black/10 transition-all"
            />
          </div>
          <div>
            <label className="block text-sm font-semibold text-gray-900 mb-1.5">
              우편번호 <span className="text-red-400">*</span>
            </label>
            <input
              type="text"
              value={zipcode}
              onChange={(e) => setZipcode(e.target.value)}
              placeholder="12345"
              maxLength={6}
              className="w-full border border-gray-200 rounded-xl px-3.5 py-2.5 text-sm text-gray-900 placeholder-gray-300 focus:outline-none focus:border-black focus:ring-2 focus:ring-black/10 transition-all"
            />
          </div>
        </div>

        <div className="flex gap-3 border-t border-gray-100 px-6 py-4">
          <button
            onClick={onClose}
            className="flex-1 border border-gray-200 rounded-xl py-2.5 text-sm font-semibold text-gray-700 hover:bg-gray-50 transition-colors"
          >
            취소
          </button>
          <button
            onClick={() => onSave(address.trim(), zipcode.trim())}
            disabled={!canSave}
            className="flex-1 bg-black text-white rounded-xl py-2.5 text-sm font-semibold hover:bg-gray-800 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
          >
            {isSaving ? '저장 중...' : '저장'}
          </button>
        </div>
      </div>
    </div>
  );
}
