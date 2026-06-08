"use client";

import { useState, type FormEvent } from "react";

export function AdminLoginClient() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setIsLoading(true);
        setErrorMessage("");

        try {
            const response = await fetch("/api/admin/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
            });

            if (!response.ok) {
                throw new Error("로그인 실패");
            }

            window.location.href = "/admin/orders";
        } catch {
            setErrorMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
            setIsLoading(false);
        }
    };



















    return (
        <div className="flex min-h-screen items-center justify-center bg-zinc-50 px-4">
            <div className="w-full max-w-sm rounded border border-zinc-200 bg-white p-8 shadow-sm">
                <div className="mb-6 text-center">
                    <p className="text-sm font-semibold text-zinc-500">Admin</p>
                    <h1 className="mt-1 text-xl font-bold text-zinc-950">Grids &amp; Circles</h1>
                    <p className="mt-2 text-sm text-zinc-500">관리자 계정으로 로그인하세요.</p>
                </div>

                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <div>
                        <label htmlFor="username" className="mb-1.5 block text-sm font-medium text-zinc-700">
                            아이디
                        </label>
                        <input
                            id="username"
                            type="text"
                            value={username}
                            onChange={(event) => setUsername(event.target.value)}
                            placeholder="admin"
                            className="h-10 w-full rounded border border-zinc-300 px-3 text-sm outline-none focus:border-zinc-950"
                            required
                        />
                    </div>

                    <div>
                        <label htmlFor="password" className="mb-1.5 block text-sm font-medium text-zinc-700">
                            비밀번호
                        </label>
                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(event) => setPassword(event.target.value)}
                            placeholder="••••••••"
                            className="h-10 w-full rounded border border-zinc-300 px-3 text-sm outline-none focus:border-zinc-950"
                            required
                        />
                    </div>

                    {errorMessage && (
                        <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                            {errorMessage}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="mt-2 h-11 rounded bg-zinc-950 text-sm font-semibold text-white transition hover:bg-zinc-800 disabled:cursor-not-allowed disabled:bg-zinc-300"
                    >
                        {isLoading ? "로그인 중..." : "로그인"}
                    </button>
                </form>
            </div>
        </div>
    );
}