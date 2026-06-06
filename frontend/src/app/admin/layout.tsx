import type { ReactNode } from "react";
import { AdminSidebar } from "./_components/AdminSidebar";

export default function AdminLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-zinc-50 text-zinc-950">
      <AdminSidebar />

      <div className="ml-64 min-h-screen overflow-x-auto">
        <div className="min-w-[1120px]">
          <header className="sticky top-0 z-50 flex h-16 items-center justify-between border-b border-zinc-200 bg-white px-8">
            <div>
              <p className="text-sm font-medium text-zinc-500">관리자 페이지</p>
            </div>
            <div className="text-sm font-medium text-zinc-500">Admin</div>
          </header>

          <main className="px-8 py-8">{children}</main>
        </div>
      </div>
    </div>
  );
}
