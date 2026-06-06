"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const menuItems = [
  {
    label: "주문 관리",
    href: "/admin/orders",
  },
  {
    label: "상품 관리",
    href: "/admin/products",
  },
];

export function AdminSidebar() {
  const pathname = usePathname();

  return (
    <aside className="fixed inset-y-0 left-0 z-50 flex w-64 shrink-0 flex-col border-r border-zinc-200 bg-white">
      <div className="border-b border-zinc-200 px-6 py-5">
        <p className="text-sm font-semibold text-zinc-500">Admin</p>
        <h1 className="mt-1 text-xl font-bold text-zinc-950">
          Grids & Circles
        </h1>
      </div>

      <nav className="flex flex-1 flex-col gap-1 px-3 py-4">
        {menuItems.map((item) => {
          const isActive = pathname.startsWith(item.href);

          return (
            <Link
              key={item.href}
              href={item.href}
              className={`rounded px-3 py-2.5 text-sm font-medium transition ${
                isActive
                  ? "bg-zinc-950 text-white"
                  : "text-zinc-600 hover:bg-zinc-100 hover:text-zinc-950"
              }`}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
