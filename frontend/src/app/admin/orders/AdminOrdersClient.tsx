"use client";

import {
  getAdminOrder,
  getAdminOrders,
  getTodayAdminOrders,
  updateAdminOrderStatus,
} from "@/api/adminOrder";
import type {
  AdminOrderDetailResponse,
  AdminOrderResponse,
  OrderStatus,
} from "@/types/adminOrder";
import { useEffect, useMemo, useState } from "react";

type OrderView = "all" | "today";
type StatusFilter = "ALL" | OrderStatus;

const ORDERS_PER_PAGE = 10;

const ORDER_STATUS_FLOW: OrderStatus[] = [
  "ORDERED",
  "CONFIRMED",
  "PREPARING_SHIPMENT",
  "SHIPPED",
  "DELIVERED",
  "CANCELED",
];

const ORDER_STATUS_META: Record<
  OrderStatus,
  {
    label: string;
    badgeClassName: string;
    nextStatus: OrderStatus | null;
    actionLabel: string;
  }
> = {
  ORDERED: {
    label: "주문완료",
    badgeClassName: "bg-amber-50 text-amber-700",
    nextStatus: "CONFIRMED",
    actionLabel: "주문 확인",
  },
  CONFIRMED: {
    label: "주문확인",
    badgeClassName: "bg-sky-50 text-sky-700",
    nextStatus: "PREPARING_SHIPMENT",
    actionLabel: "배송 준비",
  },
  PREPARING_SHIPMENT: {
    label: "배송준비중",
    badgeClassName: "bg-indigo-50 text-indigo-700",
    nextStatus: "SHIPPED",
    actionLabel: "배송 시작",
  },
  SHIPPED: {
    label: "배송중",
    badgeClassName: "bg-blue-50 text-blue-700",
    nextStatus: "DELIVERED",
    actionLabel: "배송 완료",
  },
  DELIVERED: {
    label: "배송완료",
    badgeClassName: "bg-emerald-50 text-emerald-700",
    nextStatus: null,
    actionLabel: "처리 완료",
  },
  CANCELED: {
    label: "주문취소",
    badgeClassName: "bg-rose-50 text-rose-700",
    nextStatus: null,
    actionLabel: "취소 완료",
  },
};

function formatDateTime(value: string) {
  return value.replace("T", " ").slice(0, 16);
}

function formatPrice(value: number) {
  return value.toLocaleString("ko-KR");
}

function getStatusLabel(status: OrderStatus) {
  return ORDER_STATUS_META[status].label;
}

function getStatusActionLabel(status: OrderStatus) {
  return ORDER_STATUS_META[status].actionLabel;
}

function getNextStatus(status: OrderStatus) {
  return ORDER_STATUS_META[status].nextStatus;
}

function StatusBadge({ status }: Pick<AdminOrderResponse, "status">) {
  return (
    <span
      className={`inline-flex rounded px-2 py-1 text-xs font-semibold ${ORDER_STATUS_META[status].badgeClassName}`}
    >
      {getStatusLabel(status)}
    </span>
  );
}

function DetailModal({
  order,
  onClose,
  onAdvanceStatus,
  isProcessing,
}: {
  order: AdminOrderDetailResponse;
  onClose: () => void;
  onAdvanceStatus: (order: AdminOrderDetailResponse) => void;
  isProcessing: boolean;
}) {
  const nextStatus = getNextStatus(order.status);

  return (
    <div
      className="fixed inset-0 z-[100] grid place-items-center overflow-y-auto bg-zinc-950/45 p-6"
      role="presentation"
      onMouseDown={onClose}
    >
      <section
        aria-modal="true"
        role="dialog"
        className="w-full max-w-3xl overflow-hidden rounded border border-zinc-200 bg-white shadow-2xl"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <header className="flex items-start justify-between border-b border-zinc-200 px-6 py-5">
          <div>
            <p className="text-sm font-medium text-zinc-500">
              주문번호 #{order.id}
            </p>
            <h3 className="mt-1 text-xl font-bold">주문 상세</h3>
          </div>
          <button
            type="button"
            className="rounded border border-zinc-300 px-3 py-1.5 text-sm font-medium"
            onClick={onClose}
          >
            닫기
          </button>
        </header>

        <div className="overflow-y-auto px-6 py-5">
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p className="text-zinc-500">주문자</p>
              <p className="mt-1 font-medium">{order.email}</p>
            </div>
            <div>
              <p className="text-zinc-500">상태</p>
              <div className="mt-1">
                <StatusBadge status={order.status} />
              </div>
            </div>
            <div>
              <p className="text-zinc-500">주문 시간</p>
              <p className="mt-1 font-medium">{formatDateTime(order.orderAt)}</p>
            </div>
            <div>
              <p className="text-zinc-500">배송 예정일</p>
              <p className="mt-1 font-medium">
                {formatDateTime(order.shippingDate)}
              </p>
            </div>
            <div className="col-span-2">
              <p className="text-zinc-500">배송지</p>
              <p className="mt-1 font-medium">
                {order.address} ({order.zipcode})
              </p>
            </div>
          </div>

          <div className="mt-6 overflow-hidden rounded border border-zinc-200">
            <table className="w-full table-fixed border-collapse text-sm">
              <thead className="bg-zinc-100 text-left text-zinc-600">
                <tr>
                  <th className="px-4 py-3 font-semibold">상품명</th>
                  <th className="w-20 px-4 py-3 text-right font-semibold">
                    수량
                  </th>
                  <th className="w-32 px-4 py-3 text-right font-semibold">
                    단가
                  </th>
                  <th className="w-32 px-4 py-3 text-right font-semibold">
                    금액
                  </th>
                </tr>
              </thead>
              <tbody>
                {order.items.map((item) => (
                  <tr key={item.productId} className="border-t border-zinc-100">
                    <td className="truncate px-4 py-3">{item.productName}</td>
                    <td className="px-4 py-3 text-right">{item.quantity}</td>
                    <td className="px-4 py-3 text-right">
                      {formatPrice(item.orderPrice)}원
                    </td>
                    <td className="px-4 py-3 text-right font-medium">
                      {formatPrice(item.totalPrice)}원
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <footer className="flex items-center justify-between border-t border-zinc-200 px-6 py-4">
          <p className="text-lg font-bold">총 {formatPrice(order.totalPrice)}원</p>
          <button
            type="button"
            className="rounded bg-zinc-950 px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:bg-zinc-300"
            disabled={!nextStatus || isProcessing}
            onClick={() => onAdvanceStatus(order)}
          >
            {getStatusActionLabel(order.status)}
          </button>
        </footer>
      </section>
    </div>
  );
}

export function AdminOrdersClient() {
  const [orders, setOrders] = useState<AdminOrderResponse[]>([]);
  const [orderView, setOrderView] = useState<OrderView>("all");
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("ALL");
  const [keyword, setKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedOrderIds, setSelectedOrderIds] = useState<number[]>([]);
  const [selectedOrderDetail, setSelectedOrderDetail] =
    useState<AdminOrderDetailResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const loadOrders = async (view: OrderView = orderView) => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const nextOrders =
        view === "today" ? await getTodayAdminOrders() : await getAdminOrders();

      setOrders(nextOrders);
      setSelectedOrderIds([]);
      setCurrentPage(1);
    } catch {
      setErrorMessage(
        "주문 목록을 불러오지 못했습니다. 백엔드 서버가 실행 중인지 확인해주세요.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    let isMounted = true;

    async function loadInitialOrders() {
      try {
        const nextOrders = await getAdminOrders();

        if (isMounted) {
          setOrders(nextOrders);
        }
      } catch {
        if (isMounted) {
          setErrorMessage(
            "주문 목록을 불러오지 못했습니다. 백엔드 서버가 실행 중인지 확인해주세요.",
          );
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadInitialOrders();

    return () => {
      isMounted = false;
    };
  }, []);

  const orderCounts = useMemo(() => {
    const statusCounts = ORDER_STATUS_FLOW.reduce(
      (counts, status) => ({
        ...counts,
        [status]: orders.filter((order) => order.status === status).length,
      }),
      {} as Record<OrderStatus, number>,
    );
    const totalPrice = orders.reduce((sum, order) => sum + order.totalPrice, 0);

    return {
      statusCounts,
      totalPrice,
    };
  }, [orders]);

  const filteredOrders = useMemo(() => {
    const normalizedKeyword = keyword.trim().toLowerCase();

    return orders.filter((order) => {
      const matchesStatus =
        statusFilter === "ALL" || order.status === statusFilter;
      const matchesKeyword =
        normalizedKeyword.length === 0 ||
        order.email.toLowerCase().includes(normalizedKeyword) ||
        String(order.id).includes(normalizedKeyword);

      return matchesStatus && matchesKeyword;
    });
  }, [keyword, orders, statusFilter]);

  const totalPages = Math.max(
    1,
    Math.ceil(filteredOrders.length / ORDERS_PER_PAGE),
  );

  useEffect(() => {
    setCurrentPage((page) => Math.min(page, totalPages));
  }, [totalPages]);

  const paginatedOrders = useMemo(() => {
    const startIndex = (currentPage - 1) * ORDERS_PER_PAGE;

    return filteredOrders.slice(startIndex, startIndex + ORDERS_PER_PAGE);
  }, [currentPage, filteredOrders]);

  const pageNumbers = useMemo(() => {
    const endPage = Math.min(totalPages, Math.max(5, currentPage + 2));
    const startPage = Math.max(1, Math.min(currentPage - 2, endPage - 4));

    return Array.from(
      { length: endPage - startPage + 1 },
      (_, index) => startPage + index,
    );
  }, [currentPage, totalPages]);

  const visibleStart =
    filteredOrders.length === 0 ? 0 : (currentPage - 1) * ORDERS_PER_PAGE + 1;
  const visibleEnd = Math.min(
    currentPage * ORDERS_PER_PAGE,
    filteredOrders.length,
  );

  const selectableOrderIds = paginatedOrders
    .filter((order) => getNextStatus(order.status))
    .map((order) => order.id);
  const isAllSelected =
    selectableOrderIds.length > 0 &&
    selectableOrderIds.every((id) => selectedOrderIds.includes(id));

  const changeOrderView = async (nextView: OrderView) => {
    setOrderView(nextView);
    setStatusFilter("ALL");
    setKeyword("");
    setCurrentPage(1);
    setSelectedOrderIds([]);
    await loadOrders(nextView);
  };

  const changeStatusFilter = (nextStatusFilter: StatusFilter) => {
    setStatusFilter(nextStatusFilter);
    setCurrentPage(1);
    setSelectedOrderIds([]);
  };

  const changeKeyword = (nextKeyword: string) => {
    setKeyword(nextKeyword);
    setCurrentPage(1);
    setSelectedOrderIds([]);
  };

  const toggleOrder = (id: number) => {
    setSelectedOrderIds((current) =>
      current.includes(id)
        ? current.filter((orderId) => orderId !== id)
        : [...current, id],
    );
  };

  const toggleAllOrders = () => {
    setSelectedOrderIds((current) =>
      isAllSelected
        ? current.filter((id) => !selectableOrderIds.includes(id))
        : Array.from(new Set([...current, ...selectableOrderIds])),
    );
  };

  const openDetail = async (id: number) => {
    setIsProcessing(true);
    setErrorMessage("");

    try {
      setSelectedOrderDetail(await getAdminOrder(id));
    } catch {
      setErrorMessage("주문 상세 정보를 불러오지 못했습니다.");
    } finally {
      setIsProcessing(false);
    }
  };

  const advanceSingleStatus = async (
    order: AdminOrderResponse | AdminOrderDetailResponse,
  ) => {
    const nextStatus = getNextStatus(order.status);

    if (!nextStatus) {
      return;
    }

    const confirmed = window.confirm(
      `선택한 주문을 ${getStatusLabel(nextStatus)} 상태로 변경할까요?`,
    );

    if (!confirmed) {
      return;
    }

    setIsProcessing(true);
    setErrorMessage("");

    try {
      await updateAdminOrderStatus(order.id, { status: nextStatus });
      setSelectedOrderDetail(null);
      await loadOrders(orderView);
    } catch {
      setErrorMessage("주문 상태 변경에 실패했습니다.");
    } finally {
      setIsProcessing(false);
    }
  };

  const advanceBulkStatus = async () => {
    if (selectedOrderIds.length === 0) {
      return;
    }

    const selectedOrders = orders.filter((order) =>
      selectedOrderIds.includes(order.id),
    );
    const statusUpdates = selectedOrders
      .map((order) => ({
        id: order.id,
        status: getNextStatus(order.status),
      }))
      .filter(
        (update): update is { id: number; status: OrderStatus } =>
          update.status !== null,
      );

    if (statusUpdates.length === 0) {
      return;
    }

    const confirmed = window.confirm(
      `선택한 ${statusUpdates.length}건의 주문 상태를 다음 단계로 변경할까요?`,
    );

    if (!confirmed) {
      return;
    }

    setIsProcessing(true);
    setErrorMessage("");

    try {
      await Promise.all(
        statusUpdates.map((update) =>
          updateAdminOrderStatus(update.id, { status: update.status }),
        ),
      );
      await loadOrders(orderView);
    } catch {
      setErrorMessage("일괄 주문 상태 변경에 실패했습니다.");
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <section className="flex w-full flex-col gap-6">
      <div className="flex items-end justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold">주문 관리</h2>
          <p className="mt-1 text-sm text-zinc-500">
            주문 목록과 처리 상태를 확인합니다.
          </p>
        </div>

        <div className="flex rounded border border-zinc-200 bg-white p-1 text-sm">
          <button
            type="button"
            className={`rounded px-4 py-2 font-medium ${
              orderView === "all"
                ? "bg-zinc-950 text-white"
                : "text-zinc-500 hover:bg-zinc-100"
            }`}
            onClick={() => void changeOrderView("all")}
          >
            전체 주문
          </button>
          <button
            type="button"
            className={`rounded px-4 py-2 font-medium ${
              orderView === "today"
                ? "bg-zinc-950 text-white"
                : "text-zinc-500 hover:bg-zinc-100"
            }`}
            onClick={() => void changeOrderView("today")}
          >
            오늘 처리
          </button>
        </div>
      </div>

      {errorMessage && (
        <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {errorMessage}
        </div>
      )}

      <div className="grid grid-cols-3 gap-4">
        <div className="rounded border border-zinc-200 bg-white px-5 py-4">
          <p className="text-sm font-medium text-zinc-500">전체 주문</p>
          <p className="mt-3 text-2xl font-bold">{orders.length}</p>
        </div>
        <div className="rounded border border-zinc-200 bg-white px-5 py-4">
          <p className="text-sm font-medium text-zinc-500">처리 대기</p>
          <p className="mt-3 text-2xl font-bold">
            {orders.filter((order) => getNextStatus(order.status)).length}
          </p>
        </div>
        <div className="rounded border border-zinc-200 bg-white px-5 py-4">
          <p className="text-sm font-medium text-zinc-500">총 주문 금액</p>
          <p className="mt-3 text-2xl font-bold">
            {formatPrice(orderCounts.totalPrice)}원
          </p>
        </div>
      </div>

      <div className="flex flex-wrap items-center justify-between gap-3 rounded border border-zinc-200 bg-white px-4 py-3">
        <div className="flex flex-wrap gap-2">
          {[
            ["ALL", `전체 ${orders.length}`],
            ...ORDER_STATUS_FLOW.map((status) => [
              status,
              `${getStatusLabel(status)} ${orderCounts.statusCounts[status]}`,
            ]),
          ].map(([value, label]) => (
            <button
              key={value}
              type="button"
              className={`rounded border px-3 py-2 text-sm font-medium ${
                statusFilter === value
                  ? "border-zinc-950 bg-zinc-950 text-white"
                  : "border-zinc-200 text-zinc-500 hover:bg-zinc-100"
              }`}
              onClick={() => changeStatusFilter(value as StatusFilter)}
            >
              {label}
            </button>
          ))}
        </div>

        <div className="flex gap-2">
          <input
            className="h-10 w-72 rounded border border-zinc-300 px-3 text-sm outline-none focus:border-zinc-950"
            placeholder="이메일 또는 주문번호 검색"
            value={keyword}
            onChange={(event) => changeKeyword(event.target.value)}
          />
          <button
            type="button"
            className="rounded border border-zinc-300 px-3 py-2 text-sm font-medium disabled:cursor-not-allowed disabled:text-zinc-300"
            disabled={isLoading || isProcessing}
            onClick={() => void loadOrders(orderView)}
          >
            새로고침
          </button>
        </div>
      </div>

      <div className="flex items-center justify-between rounded border border-zinc-200 bg-white px-4 py-3">
        <p className="text-sm text-zinc-500">
          선택 {selectedOrderIds.length}건 / 조회 {filteredOrders.length}건
        </p>
        <button
          type="button"
          className="rounded bg-zinc-950 px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:bg-zinc-300"
          disabled={selectedOrderIds.length === 0 || isProcessing}
          onClick={() => void advanceBulkStatus()}
        >
          선택 상태 변경
        </button>
      </div>

      <div className="overflow-hidden rounded border border-zinc-200 bg-white">
        <table className="w-full table-fixed border-collapse text-sm">
          <thead className="bg-zinc-100 text-left text-zinc-600">
            <tr>
              <th className="w-12 px-4 py-3">
                <input
                  type="checkbox"
                  aria-label="배송 대기 주문 전체 선택"
                  checked={isAllSelected}
                  disabled={selectableOrderIds.length === 0}
                  onChange={toggleAllOrders}
                />
              </th>
              <th className="w-24 px-4 py-3 font-semibold">주문번호</th>
              <th className="px-4 py-3 font-semibold">주문자</th>
              <th className="w-32 px-4 py-3 font-semibold">상태</th>
              <th className="w-36 px-4 py-3 text-right font-semibold">
                주문 금액
              </th>
              <th className="w-44 px-4 py-3 font-semibold">주문 시간</th>
              <th className="w-44 px-4 py-3 font-semibold">배송 예정일</th>
              <th className="w-48 px-4 py-3 text-right font-semibold">관리</th>
            </tr>
          </thead>
          <tbody>
            {paginatedOrders.map((order) => (
              <tr key={order.id} className="border-t border-zinc-100">
                <td className="px-4 py-3">
                  <input
                    type="checkbox"
                    aria-label={`주문 ${order.id} 선택`}
                    checked={selectedOrderIds.includes(order.id)}
                    disabled={!getNextStatus(order.status)}
                    onChange={() => toggleOrder(order.id)}
                  />
                </td>
                <td className="px-4 py-3 font-medium">#{order.id}</td>
                <td className="truncate px-4 py-3">{order.email}</td>
                <td className="px-4 py-3">
                  <StatusBadge status={order.status} />
                </td>
                <td className="px-4 py-3 text-right">
                  {formatPrice(order.totalPrice)}원
                </td>
                <td className="px-4 py-3 text-zinc-600">
                  {formatDateTime(order.orderAt)}
                </td>
                <td className="px-4 py-3 text-zinc-600">
                  {formatDateTime(order.shippingDate)}
                </td>
                <td className="px-4 py-3">
                  <div className="flex justify-end gap-2">
                    <button
                      type="button"
                      className="rounded border border-zinc-300 px-3 py-1.5 text-xs font-semibold"
                      disabled={isProcessing}
                      onClick={() => void openDetail(order.id)}
                    >
                      상세
                    </button>
                    <button
                      type="button"
                      className="rounded bg-zinc-950 px-3 py-1.5 text-xs font-semibold text-white disabled:cursor-not-allowed disabled:bg-zinc-300"
                      disabled={!getNextStatus(order.status) || isProcessing}
                      onClick={() => void advanceSingleStatus(order)}
                    >
                      {getStatusActionLabel(order.status)}
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {isLoading && (
          <div className="border-t border-zinc-100 px-4 py-10 text-center text-sm text-zinc-500">
            주문을 불러오는 중입니다.
          </div>
        )}

        {!isLoading && filteredOrders.length === 0 && (
          <div className="border-t border-zinc-100 px-4 py-10 text-center text-sm text-zinc-500">
            조건에 맞는 주문이 없습니다.
          </div>
        )}

        {!isLoading && filteredOrders.length > 0 && (
          <div className="flex items-center justify-between border-t border-zinc-200 px-4 py-3 text-sm text-zinc-500">
            <span>
              조회 {filteredOrders.length}건 중 {visibleStart}-{visibleEnd}건 표시
            </span>
            <div className="flex gap-1">
              <button
                type="button"
                className="rounded border border-zinc-300 px-3 py-1.5 disabled:cursor-not-allowed disabled:text-zinc-300"
                disabled={currentPage === 1}
                onClick={() => setCurrentPage((page) => Math.max(1, page - 1))}
              >
                이전
              </button>
              {pageNumbers.map((page) => (
                <button
                  key={page}
                  type="button"
                  className={`rounded px-3 py-1.5 ${
                    currentPage === page
                      ? "bg-zinc-950 text-white"
                      : "border border-zinc-300"
                  }`}
                  onClick={() => setCurrentPage(page)}
                >
                  {page}
                </button>
              ))}
              <button
                type="button"
                className="rounded border border-zinc-300 px-3 py-1.5 disabled:cursor-not-allowed disabled:text-zinc-300"
                disabled={currentPage === totalPages}
                onClick={() =>
                  setCurrentPage((page) => Math.min(totalPages, page + 1))
                }
              >
                다음
              </button>
            </div>
          </div>
        )}
      </div>

      {selectedOrderDetail && (
        <DetailModal
          order={selectedOrderDetail}
          isProcessing={isProcessing}
          onClose={() => setSelectedOrderDetail(null)}
          onAdvanceStatus={(order) => void advanceSingleStatus(order)}
        />
      )}
    </section>
  );
}
