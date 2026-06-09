import type {
  AdminProductDetailResponse,
  AdminProductRequest,
  AdminProductResponse,
  AdminProductSalesStatusRequest,
} from "@/types/adminProduct";

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

type AdminProductImageUploadResponse = {
  imageUrl: string;
};

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const headers = new Headers(init?.headers);

  if (!(init?.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(path, {
    cache: "no-store",
    ...init,
    headers,
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

export function uploadAdminProductImage(file: File): Promise<AdminProductImageUploadResponse> {
  const formData = new FormData();
  formData.append("image", file);

  return request<AdminProductImageUploadResponse>("/api/admin/products/images", {
    method: "POST",
    body: formData,
    headers: {},
  });
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

export function updateAdminProductSalesStatus(
  id: number,
  body: AdminProductSalesStatusRequest,
): Promise<AdminProductResponse> {
  return request<AdminProductResponse>(`/api/admin/products/${id}/sales-status`, {
    method: "PATCH",
    body: JSON.stringify(body),
  });
}
