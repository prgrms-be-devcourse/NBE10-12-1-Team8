import { proxyBackend } from "@/lib/backendProxy";

type RouteContext = {
  params: Promise<{
    id: string;
  }>;
};

export async function PATCH(request: Request, context: RouteContext) {
  const { id } = await context.params;

  return proxyBackend(`/api/admin/orders/${id}/status`, {
    method: "PATCH",
    body: await request.text(),
  });
}
