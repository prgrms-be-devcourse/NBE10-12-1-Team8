"use client";

import { getAdminStatistics } from "@/api/adminStatistics";
import type {
  DailySalesResponse,
  MonthlySalesResponse,
  ProductSalesResponse,
} from "@/types/adminStatistics";
import { useEffect, useMemo, useState } from "react";

function formatPrice(value: number) {
  return `${value.toLocaleString("ko-KR")}원`;
}

function formatMonth(value: string) {
  const [, month] = value.split("-");
  return `${Number(month)}월`;
}

function formatDate(value: string) {
  const [, month, day] = value.split("-");
  return `${Number(month)}/${Number(day)}`;
}

function getBarWidth(value: number, maxValue: number) {
  if (maxValue === 0) return "0%";
  return `${Math.max((value / maxValue) * 100, value > 0 ? 6 : 0)}%`;
}

function EmptyState({ message }: { message: string }) {
  return (
    <div className="grid min-h-48 place-items-center border-t border-zinc-100 text-sm text-zinc-500">
      {message}
    </div>
  );
}

function MonthlySalesChart({ items }: { items: MonthlySalesResponse[] }) {
  const maxSales = Math.max(...items.map((item) => item.totalSales), 0);

  return (
    <section className="rounded border border-zinc-200 bg-white">
      <div className="border-b border-zinc-200 px-5 py-4">
        <h2 className="text-base font-bold">월별 매출</h2>
        <p className="mt-1 text-sm text-zinc-500">올해 1월부터 12월까지</p>
      </div>

      <div className="grid h-80 grid-cols-12 items-end gap-3 px-5 py-5">
        {items.map((item) => {
          const height = maxSales === 0 ? 0 : (item.totalSales / maxSales) * 100;

          return (
            <div key={item.month} className="flex h-full flex-col justify-end">
              <div className="flex flex-1 items-end">
                <div
                  className="w-full rounded-t bg-zinc-900"
                  style={{ height: `${height}%`, minHeight: item.totalSales > 0 ? 12 : 0 }}
                  title={formatPrice(item.totalSales)}
                />
              </div>
              <div className="mt-3 text-center text-xs font-medium text-zinc-500">
                {formatMonth(item.month)}
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
}

function DailySalesTrend({ items }: { items: DailySalesResponse[] }) {
  const maxSales = Math.max(...items.map((item) => item.totalSales), 0);

  return (
    <section className="rounded border border-zinc-200 bg-white">
      <div className="border-b border-zinc-200 px-5 py-4">
        <h2 className="text-base font-bold">최근 7일 매출 추이</h2>
        <p className="mt-1 text-sm text-zinc-500">주문일 기준, 취소 주문 제외</p>
      </div>

      <div className="space-y-4 px-5 py-5">
        {items.map((item) => (
          <div key={item.date} className="grid grid-cols-[64px_1fr_120px] items-center gap-4">
            <div className="text-sm font-medium text-zinc-600">{formatDate(item.date)}</div>
            <div className="h-3 overflow-hidden rounded bg-zinc-100">
              <div
                className="h-full rounded bg-emerald-500"
                style={{ width: getBarWidth(item.totalSales, maxSales) }}
              />
            </div>
            <div className="text-right text-sm font-semibold">
              {formatPrice(item.totalSales)}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

function ProductSalesTable({ items }: { items: ProductSalesResponse[] }) {
  return (
    <section className="rounded border border-zinc-200 bg-white">
      <div className="border-b border-zinc-200 px-5 py-4">
        <h2 className="text-base font-bold">상품별 판매량</h2>
        <p className="mt-1 text-sm text-zinc-500">취소 주문 제외, 수량순 정렬</p>
      </div>

      {items.length === 0 ? (
        <EmptyState message="판매 데이터가 없습니다." />
      ) : (
        <table className="w-full table-fixed border-collapse text-sm">
          <thead className="bg-zinc-50 text-left text-zinc-500">
            <tr>
              <th className="px-5 py-3 font-semibold">상품명</th>
              <th className="w-28 px-5 py-3 text-right font-semibold">판매량</th>
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.productId} className="border-t border-zinc-100">
                <td className="truncate px-5 py-3 font-medium">{item.productName}</td>
                <td className="px-5 py-3 text-right font-semibold">
                  {item.quantity.toLocaleString("ko-KR")}개
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}

export function AdminStatisticsClient() {
  const [monthlySales, setMonthlySales] = useState<MonthlySalesResponse[]>([]);
  const [recentDailySales, setRecentDailySales] = useState<DailySalesResponse[]>([]);
  const [productSales, setProductSales] = useState<ProductSalesResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const totalMonthlySales = useMemo(
    () => monthlySales.reduce((sum, item) => sum + item.totalSales, 0),
    [monthlySales],
  );
  const totalRecentSales = useMemo(
    () => recentDailySales.reduce((sum, item) => sum + item.totalSales, 0),
    [recentDailySales],
  );
  const totalProductQuantity = useMemo(
    () => productSales.reduce((sum, item) => sum + item.quantity, 0),
    [productSales],
  );

  useEffect(() => {
    const loadStatistics = async () => {
      setIsLoading(true);
      setErrorMessage("");

      try {
        const statistics = await getAdminStatistics();
        setMonthlySales(statistics.monthlySales);
        setRecentDailySales(statistics.recentDailySales);
        setProductSales(statistics.productSales);
      } catch {
        setErrorMessage(
          "통계 데이터를 불러오지 못했습니다. 백엔드 서버가 실행 중인지 확인해주세요.",
        );
      } finally {
        setIsLoading(false);
      }
    };

    void loadStatistics();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="text-2xl font-bold">통계</h1>
          <p className="mt-1 text-sm text-zinc-500">
            매출 흐름과 상품 판매량을 확인합니다.
          </p>
        </div>
      </div>

      {errorMessage && (
        <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {errorMessage}
        </div>
      )}

      {isLoading ? (
        <div className="grid min-h-96 place-items-center rounded border border-zinc-200 bg-white text-sm text-zinc-500">
          통계 데이터를 불러오는 중입니다.
        </div>
      ) : (
        <>
          <div className="grid grid-cols-3 gap-4">
            <div className="rounded border border-zinc-200 bg-white px-5 py-4">
              <p className="text-sm font-medium text-zinc-500">올해 매출</p>
              <p className="mt-2 text-2xl font-bold">{formatPrice(totalMonthlySales)}</p>
            </div>
            <div className="rounded border border-zinc-200 bg-white px-5 py-4">
              <p className="text-sm font-medium text-zinc-500">최근 7일 매출</p>
              <p className="mt-2 text-2xl font-bold">{formatPrice(totalRecentSales)}</p>
            </div>
            <div className="rounded border border-zinc-200 bg-white px-5 py-4">
              <p className="text-sm font-medium text-zinc-500">누적 판매 수량</p>
              <p className="mt-2 text-2xl font-bold">
                {totalProductQuantity.toLocaleString("ko-KR")}개
              </p>
            </div>
          </div>

          <MonthlySalesChart items={monthlySales} />

          <div className="grid grid-cols-[1.2fr_0.8fr] gap-6">
            <DailySalesTrend items={recentDailySales} />
            <ProductSalesTable items={productSales} />
          </div>
        </>
      )}
    </div>
  );
}
