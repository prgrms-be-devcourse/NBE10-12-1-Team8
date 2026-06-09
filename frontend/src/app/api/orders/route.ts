import { proxyBackend } from "@/lib/backendProxy";

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const email = searchParams.get("email") ?? "";
  return proxyBackend(`/api/orders?email=${encodeURIComponent(email)}`);
}

export async function POST(request: Request) {
  const body = await request.text();
  return proxyBackend("/api/orders", {
    method: "POST",
    body,
  });
}