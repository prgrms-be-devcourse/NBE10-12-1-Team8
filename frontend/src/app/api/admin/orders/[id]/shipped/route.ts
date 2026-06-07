import { proxyBackend } from "@/lib/backendProxy";

type RouteContext = {
  params: Promise<{
    id: string;
  }>;
};

export async function PUT(_request: Request, context: RouteContext) {
  const { id } = await context.params;

  return proxyBackend(`/api/admin/orders/${id}/shipped`, {
    method: "PUT",
  });
}
