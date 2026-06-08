import type {
  AdminProductDetailResponse,
  AdminProductRequest,
  AdminProductResponse,
} from "@/types/adminProduct";

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

export function getAdminProducts(): Promise<AdminProductResponse[]> {
  return request<AdminProductResponse[]>("/api/admin/products");
}

export function getAdminProduct(id: number): Promise<AdminProductDetailResponse> {
  return request<AdminProductDetailResponse>(`/api/admin/products/${id}`);
}

export function createAdminProduct(
  body: AdminProductRequest,
): Promise<AdminProductResponse> {
  return request<AdminProductResponse>("/api/admin/products", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function updateAdminProduct(
  id: number,
  body: AdminProductRequest,
): Promise<AdminProductResponse> {
  return request<AdminProductResponse>(`/api/admin/products/${id}`, {
    method: "PUT",
    body: JSON.stringify(body),
  });
}

export function deleteAdminProduct(id: number): Promise<void> {
  return request<void>(`/api/admin/products/${id}`, {
    method: "DELETE",
  });
}
