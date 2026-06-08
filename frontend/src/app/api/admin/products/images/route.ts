import { proxyBackend } from "@/lib/backendProxy";

export async function POST(request: Request) {
  return proxyBackend("/api/admin/products/images", {
    method: "POST",
    body: await request.arrayBuffer(),
    headers: {
      "Content-Type": request.headers.get("Content-Type") ?? "",
    },
  });
}
