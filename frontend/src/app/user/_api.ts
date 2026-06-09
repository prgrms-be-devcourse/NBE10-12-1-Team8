import type { Order } from './_types';

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    cache: 'no-store',
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...init?.headers,
    },
  });

  if (!response.ok) {
    throw new Error(`API 요청 실패: ${response.status}`);
  }

  const json = await response.json();
  // RsData 래핑 여부에 관계없이 data 필드가 있으면 꺼내고, 없으면 응답 자체를 반환
  return (json?.data ?? json) as T;
}

// GET /api/orders?email={email}
export function getOrdersByEmail(email: string): Promise<Order[]> {
  return request<Order[]>(`/api/orders?email=${encodeURIComponent(email)}`);
}

// PUT /api/orders/{id}
export function updateOrderAddress(
  id: number,
  address: string,
  zipcode: string,
): Promise<void> {
  return request<void>(`/api/orders/${id}`, {
    method: 'PUT',
    body: JSON.stringify({ address, zipcode }),
  });
}

// DELETE /api/orders/{id}
export function cancelOrder(id: number): Promise<void> {
  return request<void>(`/api/orders/${id}`, { method: 'DELETE' });
}
