import { proxyBackend } from "@/lib/backendProxy";

type RouteContext = {
  params: Promise<{
    path: string[];
  }>;
};

export async function GET(_request: Request, context: RouteContext) {
  const { path } = await context.params;

  return proxyBackend(`/uploads/${path.map(encodeURIComponent).join("/")}`);
}
