import { proxyBackend } from "@/lib/backendProxy";

export async function POST(request: Request) {
  const body = await request.text();
  return proxyBackend("/api/orders", {
    method: "POST",
    body,
  });
}