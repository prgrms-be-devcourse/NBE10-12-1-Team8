'use client';

import { useState } from 'react';
import type { Order } from '../_types';
import { getOrdersByEmail, cancelOrder, updateOrderAddress } from '../_api';
import { formatDateTime, formatPrice } from '../_mocks';
import { StatusBadge } from './StatusBadge';
import { EditAddressModal } from './EditAddressModal';
import { CancelConfirmModal } from './CancelConfirmModal';

type DrawerState = 'idle' | 'loading' | 'results' | 'error';

export function MyOrderDrawer({ onClose }: { onClose: () => void }) {
  const [email, setEmail] = useState('');
  const [drawerState, setDrawerState] = useState<DrawerState>('idle');
  const [orders, setOrders] = useState<Order[]>([]);
  const [searchedEmail, setSearchedEmail] = useState('');
  const [editTarget, setEditTarget] = useState<Order | null>(null);
  const [cancelTarget, setCancelTarget] = useState<Order | null>(null);
  const [isSaving, setIsSaving] = useState(false);
  const [isCancelling, setIsCancelling] = useState(false);
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  const showToast = (message: string, type: 'success' | 'error') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 3000);
  };

  const handleSearch = async () => {
    const trimmed = email.trim();
    if (!trimmed) return;
    setDrawerState('loading');
    setOrders([]);
    setSearchedEmail(trimmed);
    try {
      const result = await getOrdersByEmail(trimmed);
      setOrders(Array.isArray(result) ? result : []);
      setDrawerState('results');
    } catch {
      setDrawerState('error');
    }
  };

  const handleSaveAddress = async (address: string, zipcode: string) => {
    if (!editTarget) return;
    setIsSaving(true);
    try {
      await updateOrderAddress(editTarget.id, address, zipcode);
      setOrders((prev) => prev.map((o) => (o.id === editTarget.id ? { ...o, address, zipcode } : o)));
      setEditTarget(null);
      showToast('배송지가 수정되었습니다.', 'success');
    } catch {
      showToast('배송지 수정에 실패했습니다. 다시 시도해주세요.', 'error');
    } finally {
      setIsSaving(false);
    }
  };

  const handleCancelConfirm = async () => {
    if (!cancelTarget) return;
    setIsCancelling(true);
    try {
      await cancelOrder(cancelTarget.id);
      setOrders((prev) => prev.filter((o) => o.id !== cancelTarget.id));
      setCancelTarget(null);
      showToast('주문이 취소되었습니다.', 'success');
    } catch {
      showToast('주문 취소에 실패했습니다. 다시 시도해주세요.', 'error');
    } finally {
      setIsCancelling(false);
    }
  };

  return (
    <>
      <div className="fixed inset-0 z-40 bg-black/40" onClick={onClose} />

      <div className="fixed right-0 top-0 z-50 h-full w-full max-w-md bg-white shadow-2xl flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between bg-black px-5 py-4 shrink-0">
          <div className="flex items-center gap-2.5">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" />
            </svg>
            <h2 className="font-bold text-white text-lg">주문 목록 조회</h2>
          </div>
          <button onClick={onClose} className="text-gray-400 hover:text-white transition-colors text-2xl leading-none">×</button>
        </div>

        {/* Scrollable body */}
        <div className="flex-1 overflow-y-auto p-5 space-y-5">
          {/* Email search */}
          <div className="bg-gray-50 rounded-2xl p-4 space-y-3">
            <p className="text-sm text-gray-600">주문 시 입력한 이메일로 주문 내역을 확인하세요.</p>
            <div className="flex gap-2">
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && void handleSearch()}
                placeholder="example@email.com"
                className="flex-1 border border-gray-200 rounded-xl px-3.5 py-2.5 text-sm text-gray-900 placeholder-gray-300 bg-white focus:outline-none focus:border-black focus:ring-2 focus:ring-black/10 transition-all"
              />
              <button
                onClick={() => void handleSearch()}
                disabled={!email.trim() || drawerState === 'loading'}
                className="bg-black text-white px-4 py-2.5 rounded-xl text-sm font-bold hover:bg-gray-800 transition-colors disabled:opacity-30 disabled:cursor-not-allowed shrink-0"
              >
                {drawerState === 'loading' ? '...' : '조회'}
              </button>
            </div>
          </div>

          {drawerState === 'loading' && (
            <div className="text-center py-12">
              <div className="inline-block w-7 h-7 border-4 border-gray-200 border-t-black rounded-full animate-spin mb-3" />
              <p className="text-sm text-gray-400">주문을 불러오는 중...</p>
            </div>
          )}

          {drawerState === 'error' && (
            <div className="bg-red-50 border border-red-200 rounded-2xl p-5 text-center">
              <p className="text-red-700 font-semibold text-sm mb-1">주문 조회에 실패했습니다.</p>
              <p className="text-xs text-red-500">잠시 후 다시 시도해주세요.</p>
            </div>
          )}

          {drawerState === 'results' && (
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <p className="text-sm text-gray-500">
                  <span className="font-semibold text-gray-800">{searchedEmail}</span> 주문 {orders.length}건
                </p>
                {orders.length > 0 && (
                  <span className="text-xs text-gray-400 bg-gray-100 rounded-lg px-2.5 py-1 font-medium">
                    주문완료만 수정·취소 가능
                  </span>
                )}
              </div>

              {orders.length === 0 ? (
                <div className="bg-white rounded-2xl border border-gray-100 p-10 text-center">
                  <p className="text-3xl mb-3">📭</p>
                  <p className="font-semibold text-gray-700 mb-1 text-sm">주문 내역이 없습니다.</p>
                  <p className="text-xs text-gray-400">해당 이메일로 접수된 주문이 없습니다.</p>
                </div>
              ) : (
                orders.map((order) => (
                  <div key={order.id} className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
                    <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100">
                      <div className="flex items-center gap-2">
                        <span className="font-bold text-gray-900 text-sm">주문 #{order.id}</span>
                        <StatusBadge status={order.status} />
                      </div>
                      <span className="text-xs text-gray-400">{formatDateTime(order.orderAt)}</span>
                    </div>

                    <div className="px-4 py-3 space-y-1.5">
                      {(order.orderItems ?? []).map((item) => (
                        <div key={item.id} className="flex items-center justify-between text-sm">
                          <span className="text-gray-700">
                            {item.product.name} <span className="text-gray-400">× {item.quantity}</span>
                          </span>
                          <span className="font-semibold text-gray-900">
                            {formatPrice(item.product.price * item.quantity)}원
                          </span>
                        </div>
                      ))}
                      <div className="flex items-center justify-between border-t border-gray-100 pt-2 mt-2">
                        <span className="text-sm font-semibold text-gray-900">합계</span>
                        <span className="text-sm font-bold text-gray-900">
                          {formatPrice(
                            (order.orderItems ?? []).reduce(
                              (sum, item) => sum + item.product.price * item.quantity,
                              0,
                            ),
                          )}원
                        </span>
                      </div>
                    </div>

                    <div className="px-4 py-2.5 bg-gray-50 border-t border-gray-100 space-y-1">
                      <div className="flex gap-2 text-xs">
                        <span className="text-gray-400 shrink-0">배송지</span>
                        <span className="text-gray-600">{order.address} ({order.zipcode})</span>
                      </div>
                      <div className="flex gap-2 text-xs">
                        <span className="text-gray-400 shrink-0">배송일</span>
                        <span className="text-gray-600">{formatDateTime(order.shippingDate)}</span>
                      </div>
                    </div>

                    {order.status === 'ORDERED' ? (
                      <div className="flex gap-2 px-4 py-3 border-t border-gray-100">
                        <button
                          onClick={() => setEditTarget(order)}
                          className="flex-1 border border-gray-200 rounded-xl py-2 text-xs font-semibold text-gray-700 hover:bg-gray-50 transition-colors"
                        >
                          배송지 수정
                        </button>
                        <button
                          onClick={() => setCancelTarget(order)}
                          className="flex-1 border border-red-200 rounded-xl py-2 text-xs font-semibold text-red-600 hover:bg-red-50 transition-colors"
                        >
                          주문 취소
                        </button>
                      </div>
                    ) : (
                      <div className="px-4 py-2.5 border-t border-gray-100">
                        <p className="text-xs text-center text-gray-400">배송이 시작되어 수정이 불가합니다.</p>
                      </div>
                    )}
                  </div>
                ))
              )}
            </div>
          )}
        </div>

        {toast && (
          <div className={`mx-5 mb-5 px-4 py-3 rounded-xl text-sm font-semibold text-center shrink-0 ${toast.type === 'success' ? 'bg-emerald-600 text-white' : 'bg-red-600 text-white'}`}>
            {toast.message}
          </div>
        )}
      </div>

      {editTarget && (
        <EditAddressModal
          order={editTarget}
          onClose={() => !isSaving && setEditTarget(null)}
          onSave={handleSaveAddress}
          isSaving={isSaving}
        />
      )}
      {cancelTarget && (
        <CancelConfirmModal
          order={cancelTarget}
          onClose={() => !isCancelling && setCancelTarget(null)}
          onConfirm={handleCancelConfirm}
          isCancelling={isCancelling}
        />
      )}
    </>
  );
}
