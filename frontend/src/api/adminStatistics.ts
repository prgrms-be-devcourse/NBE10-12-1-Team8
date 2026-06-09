import type { AdminStatisticsResponse } from "@/types/adminStatistics";

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

async function request<T>(path: string): Promise<T> {
  const response = await fetch(path, {
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error(`API 요청 실패: ${response.status}`);
  }

  const result = (await response.json()) as RsData<T>;

  return result.data;
}

export function getAdminStatistics(): Promise<AdminStatisticsResponse> {
  return request<AdminStatisticsResponse>("/api/admin/statistics");
}
