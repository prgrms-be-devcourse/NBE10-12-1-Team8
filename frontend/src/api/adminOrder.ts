import type {
  AdminOrderBulkShippedRequest,
  AdminOrderBulkShippedResponse,
  AdminOrderDetailResponse,
  AdminOrderResponse,
  AdminOrderStatusResponse,
} from "@/types/adminOrder";

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    cache: "no-store",
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...init?.headers,
    },
  });

  if (!response.ok) {
    throw new Error(`API 요청 실패: ${response.status}`);
  }

  const result = (await response.json()) as RsData<T>;

  return result.data;
}

export function getAdminOrders(): Promise<AdminOrderResponse[]> {
  return request<AdminOrderResponse[]>("/api/admin/orders");
}

export function getTodayAdminOrders(): Promise<AdminOrderResponse[]> {
  return request<AdminOrderResponse[]>("/api/admin/orders/today");
}

export function getAdminOrder(id: number): Promise<AdminOrderDetailResponse> {
  return request<AdminOrderDetailResponse>(`/api/admin/orders/${id}`);
}

export function shipAdminOrder(id: number): Promise<AdminOrderStatusResponse> {
  return request<AdminOrderStatusResponse>(`/api/admin/orders/${id}/shipped`, {
    method: "PUT",
  });
}

export function shipAdminOrders(
  body: AdminOrderBulkShippedRequest,
): Promise<AdminOrderBulkShippedResponse> {
  return request<AdminOrderBulkShippedResponse>("/api/admin/orders/shipped", {
    method: "PUT",
    body: JSON.stringify(body),
  });
}
