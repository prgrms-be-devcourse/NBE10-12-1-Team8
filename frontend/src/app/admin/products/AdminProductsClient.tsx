"use client";

import {
  createAdminProduct,
  deleteAdminProduct,
  getAdminProduct,
  getAdminProducts,
  updateAdminProduct,
} from "@/api/adminProduct";
import type {
  AdminProductDetailResponse,
  AdminProductRequest,
} from "@/types/adminProduct";
import { useEffect, useMemo, useState } from "react";

type ProductFormState = {
  name: string;
  price: string;
  description: string;
  imageUrl: string;
};

const emptyForm: ProductFormState = {
  name: "",
  price: "",
  description: "",
  imageUrl: "",
};

const PRODUCTS_PER_PAGE = 10;

function formatPrice(value: number) {
  return value.toLocaleString("ko-KR");
}

function formatDate(value?: string) {
  if (!value) {
    return "-";
  }

  return value.slice(0, 10);
}

function toRequest(form: ProductFormState): AdminProductRequest {
  return {
    name: form.name.trim(),
    price: Number(form.price),
    description: form.description.trim(),
    imageUrl: form.imageUrl.trim(),
  };
}

function ProductImage({ imageUrl, name }: { imageUrl: string; name: string }) {
  if (!imageUrl) {
    return (
      <div className="grid h-10 w-10 place-items-center rounded border border-zinc-200 bg-zinc-100 text-xs text-zinc-400">
        IMG
      </div>
    );
  }

  return (
    <div
      aria-label={`${name} 이미지`}
      className="h-10 w-10 rounded border border-zinc-200 bg-zinc-100 bg-cover bg-center"
      style={{
        backgroundImage: `url(${imageUrl})`,
      }}
    />
  );
}

function ProductFormModal({
  title,
  description,
  form,
  isProcessing,
  onChange,
  onClose,
  onSubmit,
}: {
  title: string;
  description: string;
  form: ProductFormState;
  isProcessing: boolean;
  onChange: (form: ProductFormState) => void;
  onClose: () => void;
  onSubmit: () => void;
}) {
  const updateField = (field: keyof ProductFormState, value: string) => {
    onChange({
      ...form,
      [field]: value,
    });
  };

  return (
    <div
      className="fixed inset-0 z-[100] grid place-items-center overflow-y-auto bg-zinc-950/45 p-6"
      role="presentation"
      onMouseDown={onClose}
    >
      <section
        aria-modal="true"
        role="dialog"
        className="w-full max-w-2xl overflow-hidden rounded border border-zinc-200 bg-white shadow-2xl"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <header className="flex items-start justify-between border-b border-zinc-200 px-6 py-5">
          <div>
            <h3 className="text-lg font-bold">{title}</h3>
            <p className="mt-1 text-sm text-zinc-500">{description}</p>
          </div>
          <button
            type="button"
            className="rounded px-2 py-1 text-lg text-zinc-400 hover:bg-zinc-100 hover:text-zinc-700"
            onClick={onClose}
          >
            x
          </button>
        </header>

        <div className="flex flex-col gap-4 px-6 py-5">
          <div className="grid grid-cols-2 gap-4">
            <label className="flex flex-col gap-2 text-sm font-semibold">
              상품명
              <input
                className="h-10 rounded border border-zinc-300 px-3 text-sm font-normal outline-none focus:border-zinc-950"
                placeholder="예) 에티오피아 예가체프"
                value={form.name}
                onChange={(event) => updateField("name", event.target.value)}
              />
            </label>
            <label className="flex flex-col gap-2 text-sm font-semibold">
              가격 (원)
              <input
                className="h-10 rounded border border-zinc-300 px-3 text-sm font-normal outline-none focus:border-zinc-950"
                inputMode="numeric"
                placeholder="예) 32000"
                value={form.price}
                onChange={(event) => updateField("price", event.target.value)}
              />
            </label>
          </div>

          <label className="flex flex-col gap-2 text-sm font-semibold">
            상품 설명
            <textarea
              className="h-24 resize-none rounded border border-zinc-300 px-3 py-3 text-sm font-normal outline-none focus:border-zinc-950"
              placeholder="상품에 대한 간략한 설명을 입력하세요..."
              value={form.description}
              onChange={(event) =>
                updateField("description", event.target.value)
              }
            />
          </label>

          <label className="flex flex-col gap-2 text-sm font-semibold">
            이미지 URL
            <input
              className="h-10 rounded border border-zinc-300 px-3 text-sm font-normal outline-none focus:border-zinc-950"
              placeholder="https://example.com/image.jpg"
              value={form.imageUrl}
              onChange={(event) => updateField("imageUrl", event.target.value)}
            />
          </label>

          <div className="flex flex-col gap-2 text-sm font-semibold">
            이미지 미리보기
            <div className="grid h-28 place-items-center rounded border border-dashed border-zinc-300 bg-zinc-50 text-sm text-zinc-400">
              {form.imageUrl ? (
                <div
                  aria-label="이미지 미리보기"
                  className="h-full w-full rounded bg-contain bg-center bg-no-repeat"
                  style={{
                    backgroundImage: `url(${form.imageUrl})`,
                  }}
                />
              ) : (
                "이미지 미리보기가 여기에 표시됩니다"
              )}
            </div>
          </div>
        </div>

        <footer className="flex justify-end gap-2 border-t border-zinc-200 px-6 py-4">
          <button
            type="button"
            className="rounded border border-zinc-300 px-4 py-2 text-sm font-semibold"
            onClick={onClose}
          >
            취소
          </button>
          <button
            type="button"
            className="rounded bg-zinc-950 px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:bg-zinc-300"
            disabled={isProcessing}
            onClick={onSubmit}
          >
            저장
          </button>
        </footer>
      </section>
    </div>
  );
}

function DeleteConfirmModal({
  product,
  isProcessing,
  onClose,
  onDelete,
}: {
  product: AdminProductDetailResponse;
  isProcessing: boolean;
  onClose: () => void;
  onDelete: () => void;
}) {
  return (
    <div
      className="fixed inset-0 z-[100] grid place-items-center overflow-y-auto bg-zinc-950/45 p-6"
      role="presentation"
      onMouseDown={onClose}
    >
      <section
        aria-modal="true"
        role="dialog"
        className="w-full max-w-lg overflow-hidden rounded border border-zinc-200 bg-white shadow-2xl"
        onMouseDown={(event) => event.stopPropagation()}
      >
        <header className="flex items-start justify-between border-b border-zinc-200 px-6 py-5">
          <div>
            <h3 className="text-lg font-bold">상품 삭제</h3>
            <p className="mt-1 text-sm text-zinc-500">
              선택한 상품을 삭제합니다.
            </p>
          </div>
          <button
            type="button"
            className="rounded px-2 py-1 text-lg text-zinc-400 hover:bg-zinc-100 hover:text-zinc-700"
            onClick={onClose}
          >
            x
          </button>
        </header>

        <div className="px-6 py-5">
          <p className="text-sm">이 상품을 정말 삭제하시겠습니까?</p>
          <div className="mt-4 rounded border border-zinc-200 bg-zinc-50 px-4 py-3">
            <p className="font-semibold">{product.name}</p>
            <p className="mt-1 text-sm text-zinc-500">상품 ID: #{product.id}</p>
          </div>
          <p className="mt-4 text-sm text-zinc-500">
            이 작업은 되돌릴 수 없습니다.
          </p>
        </div>

        <footer className="flex justify-end gap-2 border-t border-zinc-200 px-6 py-4">
          <button
            type="button"
            className="rounded border border-zinc-300 px-4 py-2 text-sm font-semibold"
            onClick={onClose}
          >
            취소
          </button>
          <button
            type="button"
            className="rounded bg-zinc-950 px-4 py-2 text-sm font-semibold text-white disabled:cursor-not-allowed disabled:bg-zinc-300"
            disabled={isProcessing}
            onClick={onDelete}
          >
            삭제
          </button>
        </footer>
      </section>
    </div>
  );
}

export function AdminProductsClient() {
  const [products, setProducts] = useState<AdminProductDetailResponse[]>([]);
  const [keyword, setKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [form, setForm] = useState<ProductFormState>(emptyForm);
  const [editingProduct, setEditingProduct] =
    useState<AdminProductDetailResponse | null>(null);
  const [deletingProduct, setDeletingProduct] =
    useState<AdminProductDetailResponse | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const loadProducts = async () => {
    setIsLoading(true);
    setErrorMessage("");

    try {
      const productList = await getAdminProducts();
      const productDetails = await Promise.all(
        productList.map((product) => getAdminProduct(product.id)),
      );

      setProducts(productDetails);
      setCurrentPage(1);
    } catch {
      setErrorMessage(
        "상품 목록을 불러오지 못했습니다. 백엔드 서버가 실행 중인지 확인해주세요.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    let isMounted = true;

    async function loadInitialProducts() {
      setIsLoading(true);

      try {
        const productList = await getAdminProducts();
        const productDetails = await Promise.all(
          productList.map((product) => getAdminProduct(product.id)),
        );

        if (isMounted) {
          setProducts(productDetails);
        }
      } catch {
        if (isMounted) {
          setErrorMessage(
            "상품 목록을 불러오지 못했습니다. 백엔드 서버가 실행 중인지 확인해주세요.",
          );
        }
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    void loadInitialProducts();

    return () => {
      isMounted = false;
    };
  }, []);

  const filteredProducts = useMemo(() => {
    const normalizedKeyword = keyword.trim().toLowerCase();

    if (!normalizedKeyword) {
      return products;
    }

    return products.filter((product) =>
      [product.name, product.description, String(product.id)]
        .join(" ")
        .toLowerCase()
        .includes(normalizedKeyword),
    );
  }, [keyword, products]);

  const totalPages = Math.max(
    1,
    Math.ceil(filteredProducts.length / PRODUCTS_PER_PAGE),
  );

  useEffect(() => {
    setCurrentPage((page) => Math.min(page, totalPages));
  }, [totalPages]);

  const paginatedProducts = useMemo(() => {
    const startIndex = (currentPage - 1) * PRODUCTS_PER_PAGE;

    return filteredProducts.slice(startIndex, startIndex + PRODUCTS_PER_PAGE);
  }, [currentPage, filteredProducts]);

  const pageNumbers = useMemo(() => {
    const endPage = Math.min(totalPages, Math.max(5, currentPage + 2));
    const startPage = Math.max(1, Math.min(currentPage - 2, endPage - 4));

    return Array.from(
      { length: endPage - startPage + 1 },
      (_, index) => startPage + index,
    );
  }, [currentPage, totalPages]);

  const visibleStart =
    filteredProducts.length === 0
      ? 0
      : (currentPage - 1) * PRODUCTS_PER_PAGE + 1;
  const visibleEnd = Math.min(
    currentPage * PRODUCTS_PER_PAGE,
    filteredProducts.length,
  );

  const changeKeyword = (nextKeyword: string) => {
    setKeyword(nextKeyword);
    setCurrentPage(1);
  };

  const openCreateModal = () => {
    setEditingProduct(null);
    setForm(emptyForm);
    setIsFormOpen(true);
  };

  const openEditModal = (product: AdminProductDetailResponse) => {
    setEditingProduct(product);
    setForm({
      name: product.name,
      price: String(product.price),
      description: product.description ?? "",
      imageUrl: product.imageUrl ?? "",
    });
    setIsFormOpen(true);
  };

  const closeFormModal = () => {
    setIsFormOpen(false);
    setEditingProduct(null);
    setForm(emptyForm);
  };

  const submitProduct = async () => {
    const request = toRequest(form);

    if (!request.name || Number.isNaN(request.price) || request.price < 0) {
      setErrorMessage("상품명과 0원 이상의 가격을 입력해주세요.");
      return;
    }

    setIsProcessing(true);
    setErrorMessage("");

    try {
      if (editingProduct) {
        await updateAdminProduct(editingProduct.id, request);
      } else {
        await createAdminProduct(request);
      }

      closeFormModal();
      await loadProducts();
    } catch {
      setErrorMessage("상품 저장에 실패했습니다.");
    } finally {
      setIsProcessing(false);
    }
  };

  const confirmDelete = async () => {
    if (!deletingProduct) {
      return;
    }

    setIsProcessing(true);
    setErrorMessage("");

    try {
      await deleteAdminProduct(deletingProduct.id);
      setDeletingProduct(null);
      await loadProducts();
    } catch {
      setErrorMessage("상품 삭제에 실패했습니다.");
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <section className="flex w-full flex-col gap-6">
      <div className="flex items-end justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold">상품 관리</h2>
          <p className="mt-1 text-sm text-zinc-500">
            커피 원두 상품을 관리합니다.
          </p>
        </div>
        <button
          type="button"
          className="rounded bg-zinc-950 px-4 py-2 text-sm font-semibold text-white"
          onClick={openCreateModal}
        >
          + 상품 추가
        </button>
      </div>

      {errorMessage && (
        <div className="rounded border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {errorMessage}
        </div>
      )}

      <div className="flex items-center justify-between rounded border border-zinc-200 bg-white px-4 py-3">
        <input
          className="h-10 w-80 rounded border border-zinc-300 px-3 text-sm outline-none focus:border-zinc-950"
          placeholder="상품명 검색..."
          value={keyword}
          onChange={(event) => changeKeyword(event.target.value)}
        />
        <button
          type="button"
          className="rounded border border-zinc-300 px-3 py-2 text-sm font-medium disabled:cursor-not-allowed disabled:text-zinc-300"
          disabled={isLoading || isProcessing}
          onClick={() => void loadProducts()}
        >
          새로고침
        </button>
      </div>

      <div className="overflow-hidden rounded border border-zinc-200 bg-white">
        <table className="w-full table-fixed border-collapse text-sm">
          <thead className="bg-zinc-100 text-left text-zinc-600">
            <tr>
              <th className="w-16 px-4 py-3 font-semibold">ID</th>
              <th className="w-20 px-4 py-3 font-semibold">이미지</th>
              <th className="w-56 px-4 py-3 font-semibold">상품명</th>
              <th className="w-32 px-4 py-3 text-right font-semibold">가격</th>
              <th className="px-4 py-3 font-semibold">설명</th>
              <th className="w-32 px-4 py-3 font-semibold">등록일</th>
              <th className="w-32 px-4 py-3 font-semibold">수정일</th>
              <th className="w-40 px-4 py-3 text-right font-semibold">작업</th>
            </tr>
          </thead>
          <tbody>
            {paginatedProducts.map((product) => (
              <tr key={product.id} className="border-t border-zinc-100">
                <td className="px-4 py-3 text-zinc-500">#{product.id}</td>
                <td className="px-4 py-3">
                  <ProductImage imageUrl={product.imageUrl} name={product.name} />
                </td>
                <td className="truncate px-4 py-3 font-semibold">
                  {product.name}
                </td>
                <td className="px-4 py-3 text-right font-medium">
                  {formatPrice(product.price)}원
                </td>
                <td className="truncate px-4 py-3 text-zinc-500">
                  {product.description}
                </td>
                <td className="px-4 py-3 text-zinc-500">
                  {formatDate(product.createDate)}
                </td>
                <td className="px-4 py-3 text-zinc-500">
                  {formatDate(product.modifyDate)}
                </td>
                <td className="px-4 py-3">
                  <div className="flex justify-end gap-2">
                    <button
                      type="button"
                      className="rounded border border-zinc-300 px-3 py-1.5 text-xs font-semibold"
                      onClick={() => openEditModal(product)}
                    >
                      수정
                    </button>
                    <button
                      type="button"
                      className="rounded border border-zinc-300 px-3 py-1.5 text-xs font-semibold text-zinc-600"
                      onClick={() => setDeletingProduct(product)}
                    >
                      삭제
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {isLoading && (
          <div className="border-t border-zinc-100 px-4 py-20 text-center text-sm text-zinc-500">
            상품을 불러오는 중입니다.
          </div>
        )}

        {!isLoading && filteredProducts.length === 0 && (
          <div className="grid min-h-72 place-items-center border-t border-zinc-100 px-4 py-16 text-center">
            <div>
              <div className="mx-auto grid h-14 w-14 place-items-center rounded-full border border-dashed border-zinc-300 bg-zinc-50 text-sm text-zinc-400">
                BOX
              </div>
              <p className="mt-5 font-semibold">아직 등록된 상품이 없습니다.</p>
              <p className="mt-1 text-sm text-zinc-500">
                첫 번째 상품을 추가해 보세요.
              </p>
              <button
                type="button"
                className="mt-5 rounded bg-zinc-950 px-4 py-2 text-sm font-semibold text-white"
                onClick={openCreateModal}
              >
                + 상품 추가
              </button>
            </div>
          </div>
        )}

        {!isLoading && filteredProducts.length > 0 && (
          <div className="flex items-center justify-between border-t border-zinc-200 px-4 py-3 text-sm text-zinc-500">
            <span>
              조회 {filteredProducts.length}개 중 {visibleStart}-{visibleEnd}개 표시
            </span>
            <div className="flex gap-1">
              <button
                type="button"
                className="rounded border border-zinc-300 px-3 py-1.5 disabled:cursor-not-allowed disabled:text-zinc-300"
                disabled={currentPage === 1}
                onClick={() => setCurrentPage((page) => Math.max(1, page - 1))}
              >
                이전
              </button>
              {pageNumbers.map((page) => (
                <button
                  key={page}
                  type="button"
                  className={`rounded px-3 py-1.5 ${
                    currentPage === page
                      ? "bg-zinc-950 text-white"
                      : "border border-zinc-300"
                  }`}
                  onClick={() => setCurrentPage(page)}
                >
                  {page}
                </button>
              ))}
              <button
                type="button"
                className="rounded border border-zinc-300 px-3 py-1.5 disabled:cursor-not-allowed disabled:text-zinc-300"
                disabled={currentPage === totalPages}
                onClick={() =>
                  setCurrentPage((page) => Math.min(totalPages, page + 1))
                }
              >
                다음
              </button>
            </div>
          </div>
        )}
      </div>

      {isFormOpen && (
        <ProductFormModal
          title={editingProduct ? "상품 수정" : "새 상품 추가"}
          description={
            editingProduct
              ? "상품 정보를 수정합니다."
              : "새로운 상품 정보를 입력하세요."
          }
          form={form}
          isProcessing={isProcessing}
          onChange={setForm}
          onClose={closeFormModal}
          onSubmit={() => void submitProduct()}
        />
      )}

      {deletingProduct && (
        <DeleteConfirmModal
          product={deletingProduct}
          isProcessing={isProcessing}
          onClose={() => setDeletingProduct(null)}
          onDelete={() => void confirmDelete()}
        />
      )}
    </section>
  );
}
