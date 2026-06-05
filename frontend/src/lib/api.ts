const BASE_URL = "http://localhost:8080";

// ── 타입 정의 ──────────────────────────────────────────

export type Product = {
  id: number;
  name: string;
  price: number;
  description: string;
  imageUrl: string;
};

export type OrderItemResponse = {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
  totalPrice: number;
};

export type Order = {
  id: number;
  email: string;
  shippingDate: string;
  address: string;
  zipcode: string;
  orderAt: string;
  status: string;
  totalPrice: number;
  items: OrderItemResponse[];
};

export type OrderItemRequest = {
  productId: number;
  quantity: number;
};

export type CreateOrderRequest = {
  email: string;
  address: string;
  zipcode: string;
  items: OrderItemRequest[];
};

export type AdminProduct = Product & {
  description: string;
  imageUrl: string;
};

export type AdminOrder = {
  id: number;
  email: string;
  shippingDate: string;
  address: string;
  zipcode: string;
  totalPrice: number;
  status: string;
  orderAt: string;
};

export type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

// ── 유틸 ───────────────────────────────────────────────

async function request<T>(url: string, options?: RequestInit): Promise<RsData<T>> {
  const res = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  return res.json();
}

// ── 고객용 API ─────────────────────────────────────────

export const getProducts = () =>
  request<Product[]>(`${BASE_URL}/api/products`);

export const createOrder = (body: CreateOrderRequest) =>
  request<Order>(`${BASE_URL}/api/orders`, {
    method: "POST",
    body: JSON.stringify(body),
  });

export const getOrdersByEmail = (email: string) =>
  request<Order[]>(`${BASE_URL}/api/orders?email=${encodeURIComponent(email)}`);

export const cancelOrder = (id: number) =>
  request<void>(`${BASE_URL}/api/orders/${id}`, { method: "DELETE" });

// ── 관리자용 API ───────────────────────────────────────

export const adminGetProducts = () =>
  request<AdminProduct[]>(`${BASE_URL}/api/admin/products`);

export const adminCreateProduct = (body: {
  name: string;
  price: number;
  description: string;
  imageUrl: string;
}) =>
  request<AdminProduct>(`${BASE_URL}/api/admin/products`, {
    method: "POST",
    body: JSON.stringify(body),
  });

export const adminUpdateProduct = (
  id: number,
  body: { name: string; price: number; description: string; imageUrl: string }
) =>
  request<AdminProduct>(`${BASE_URL}/api/admin/products/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
  });

export const adminDeleteProduct = (id: number) =>
  request<void>(`${BASE_URL}/api/admin/products/${id}`, { method: "DELETE" });

export const adminGetOrders = () =>
  request<AdminOrder[]>(`${BASE_URL}/api/admin/orders`);

export const adminGetTodayOrders = () =>
  request<AdminOrder[]>(`${BASE_URL}/api/admin/orders/today`);

export const adminShipOrder = (id: number) =>
  request<{ id: number; status: string }>(`${BASE_URL}/api/admin/orders/${id}/shipped`, {
    method: "PUT",
  });

export const adminShipOrders = (orderIds: number[]) =>
  request<{ processedCount: number; orderIds: number[] }>(
    `${BASE_URL}/api/admin/orders/shipped`,
    { method: "PUT", body: JSON.stringify({ orderIds }) }
  );
