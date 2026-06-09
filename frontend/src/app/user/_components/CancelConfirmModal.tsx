import type { Order } from '../_types';

export function CancelConfirmModal({
  order,
  onClose,
  onConfirm,
  isCancelling,
}: {
  order: Order;
  onClose: () => void;
  onConfirm: () => void;
  isCancelling: boolean;
}) {
  return (
    <div
      className="fixed inset-0 z-[200] flex items-center justify-center bg-black/50 px-4"
      onMouseDown={onClose}
    >
      <div
        className="w-full max-w-sm rounded-2xl bg-white shadow-2xl"
        onMouseDown={(e) => e.stopPropagation()}
      >
        <div className="px-6 pt-6 pb-4 text-center">
          <div className="text-4xl mb-3">🗑️</div>
          <h3 className="text-lg font-bold text-gray-900 mb-1">주문을 취소하시겠습니까?</h3>
          <p className="text-sm text-gray-500">
            주문 <span className="font-semibold text-gray-800">#{order.id}</span> 취소 시 되돌릴 수 없습니다.
          </p>
        </div>
        <div className="flex gap-3 border-t border-gray-100 px-6 py-4">
          <button
            onClick={onClose}
            className="flex-1 border border-gray-200 rounded-xl py-2.5 text-sm font-semibold text-gray-700 hover:bg-gray-50 transition-colors"
          >
            돌아가기
          </button>
          <button
            onClick={onConfirm}
            disabled={isCancelling}
            className="flex-1 bg-red-600 text-white rounded-xl py-2.5 text-sm font-semibold hover:bg-red-700 transition-colors disabled:opacity-30 disabled:cursor-not-allowed"
          >
            {isCancelling ? '취소 중...' : '주문 취소'}
          </button>
        </div>
      </div>
    </div>
  );
}
