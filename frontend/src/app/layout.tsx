import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Grids & Circles',
  description: 'Premium Coffee Beans — 신선한 원두를 직접 주문하세요',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
}
