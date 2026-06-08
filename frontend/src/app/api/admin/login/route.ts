import { NextResponse } from "next/server";

const ADMIN_USERNAME = process.env.ADMIN_USERNAME;
const ADMIN_PASSWORD = process.env.ADMIN_PASSWORD;
const SESSION_COOKIE_NAME = "admin_session";
const SESSION_COOKIE_VALUE = process.env.ADMIN_SESSION_SECRET ?? "authenticated";

export async function POST(request: Request) {
    const { username, password } = await request.json();

    const isValid = username === ADMIN_USERNAME && password === ADMIN_PASSWORD;

    if (!isValid) {
        return NextResponse.json(
            { message: "아이디 또는 비밀번호가 올바르지 않습니다." },
            { status: 401 },
        );
    }

    const response = NextResponse.json({ message: "로그인 성공" });

    response.cookies.set(SESSION_COOKIE_NAME, SESSION_COOKIE_VALUE, {
        httpOnly: true,
        sameSite: "lax",
        path: "/",
        maxAge: 60 * 60 * 8, // 8시간 동안 로그인 유지
    });

    return response;
}