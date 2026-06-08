import { proxyBackend } from "@/lib/backendProxy";

export function GET() {
  return proxyBackend("/api/admin/products");
}

export async function POST(request: Request) {
  return proxyBackend("/api/admin/products", {
    method: "POST",
    body: await request.text(),
  });
}
