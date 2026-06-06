import { proxyBackend } from "@/lib/backendProxy";

export async function PUT(request: Request) {
  return proxyBackend("/api/admin/orders/shipped", {
    method: "PUT",
    body: await request.text(),
  });
}
