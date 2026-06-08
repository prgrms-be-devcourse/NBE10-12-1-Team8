const BACKEND_BASE_URL = process.env.BACKEND_BASE_URL ?? "http://localhost:8080";

export async function proxyBackend(path: string, init?: RequestInit) {
  const headers = new Headers(init?.headers);

  if (!headers.has("Content-Type") && init?.body !== undefined) {
    headers.set("Content-Type", "application/json");
  }

  const response = await fetch(`${BACKEND_BASE_URL}${path}`, {
    cache: "no-store",
    ...init,
    headers,
  });

  const body = await response.arrayBuffer();

  return new Response(body, {
    status: response.status,
    headers: {
      "Content-Type":
        response.headers.get("Content-Type") ?? "application/octet-stream",
    },
  });
}
