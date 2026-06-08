import type { OrderStatus } from '../_types';

export function StatusBadge({ status }: { status: OrderStatus }) {
  if (status === 'SHIPPED') {
    return (
      <span className="inline-flex rounded-full px-2.5 py-0.5 text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
        배송완료
      </span>
    );
  }
  return (
    <span className="inline-flex rounded-full px-2.5 py-0.5 text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200">
      주문완료
    </span>
  );
}
