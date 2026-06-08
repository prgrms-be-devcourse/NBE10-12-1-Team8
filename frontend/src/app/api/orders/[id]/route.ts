import { proxyBackend } from "@/lib/backendProxy";

type RouteContext = {
  params: Promise<{ id: string }>;
};

export async function PUT(request: Request, context: RouteContext) {
  const { id } = await context.params;
  const body = await request.text();
  return proxyBackend(`/api/orders/${id}`, { method: "PUT", body });
}

export async function DELETE(_request: Request, context: RouteContext) {
  const { id } = await context.params;
  return proxyBackend(`/api/orders/${id}`, { method: "DELETE" });
}
