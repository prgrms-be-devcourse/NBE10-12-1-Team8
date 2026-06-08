import { NextResponse, type NextRequest } from "next/server";

const SESSION_COOKIE_NAME = "admin_session";
const SESSION_COOKIE_VALUE = process.env.ADMIN_SESSION_SECRET ?? "authenticated";

// 로그인 화면 자신과 로그인 처리 API는 검사에서 제외해야 함
const PUBLIC_PATHS = ["/login", "/api/admin/login"];

export function middleware(request: NextRequest) {
    const { pathname } = request.nextUrl;

    if (PUBLIC_PATHS.some((path) => pathname.startsWith(path))) {
        return NextResponse.next();
    }

    const sessionCookie = request.cookies.get(SESSION_COOKIE_NAME);
    const isAuthenticated = sessionCookie?.value === SESSION_COOKIE_VALUE;

    if (isAuthenticated) {
        return NextResponse.next();
    }

    if (pathname.startsWith("/api/")) {
        return NextResponse.json({ message: "로그인이 필요합니다." }, { status: 401 });
    }

    return NextResponse.redirect(new URL("/login", request.url));
}

export const config = {
    matcher: ["/admin/:path*", "/api/admin/:path*"],
};