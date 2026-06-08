import { proxyBackend } from "@/lib/backendProxy";

export function GET() {
  return proxyBackend("/api/products");
}