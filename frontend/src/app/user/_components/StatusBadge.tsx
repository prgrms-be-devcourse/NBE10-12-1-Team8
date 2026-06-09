import type { OrderStatus } from '../_types';

const STATUS_MAP: Record<OrderStatus, { label: string; className: string }> = {
  ORDERED:            { label: '주문완료',    className: 'bg-amber-50 text-amber-700 border-amber-200' },
  CONFIRMED:          { label: '주문확인',    className: 'bg-sky-50 text-sky-700 border-sky-200' },
  PREPARING_SHIPMENT: { label: '배송준비중',  className: 'bg-indigo-50 text-indigo-700 border-indigo-200' },
  SHIPPED:            { label: '배송중',      className: 'bg-blue-50 text-blue-700 border-blue-200' },
  DELIVERED:          { label: '배송완료',    className: 'bg-emerald-50 text-emerald-700 border-emerald-200' },
  CANCELED:           { label: '주문취소',    className: 'bg-rose-50 text-rose-700 border-rose-200' },
};

export function StatusBadge({ status }: { status: OrderStatus }) {
  const { label, className } = STATUS_MAP[status] ?? STATUS_MAP.ORDERED;
  return (
    <span className={`inline-flex rounded-full px-2.5 py-0.5 text-xs font-semibold border ${className}`}>
      {label}
    </span>
  );
}