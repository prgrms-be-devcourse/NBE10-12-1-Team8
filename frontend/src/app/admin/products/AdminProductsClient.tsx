"use client";

import {
  createAdminProduct,
  deleteAdminProduct,
  getAdminProduct,
  getAdminProducts,
  uploadAdminProductImage,
  updateAdminProduct,
} from "@/api/adminProduct";
import type {
  AdminProductDetailResponse,
  AdminProductRequest,
} from "@/types/adminProduct";
import { useEffect, useMemo, useRef, useState } from "react";

type ProductFormState = {
  name: string;
  price: string;
  description: string;
  imageUrl: string;
  imageFile: File | null;
  imagePreviewUrl: string;
};

type ProductFormErrors = Partial<
  Record<"name" | "price" | "description" | "image", string>
>;

const emptyForm: ProductFormState = {
  name: "",
  price: "",
  description: "",
  imageUrl: "",
  imageFile: null,
  imagePreviewUrl: "",
};

const PRODUCTS_PER_PAGE = 10;
const ALLOWED_IMAGE_TYPES = new Set([
  "image/jpeg",
  "image/png",
  "image/webp",
  "image/gif",
]);
const IMAGE_FALLBACK_SRC =
  "data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='160' height='160'%3E%3Crect width='160' height='160' fill='%23f4f4f5'/%3E%3Ctext x='80' y='84' text-anchor='middle' font-family='sans-serif' font-size='14' fill='%23a1a1aa'%3EIMG%3C/text%3E%3C/svg%3E";

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

function ProductImage({
  imageUrl,
  name,
  className = "h-10 w-10",
  fit = "cover",
}: {
  imageUrl: string;
  name: string;
  className?: string;
  fit?: "cover" | "contain";
}) {
  const [hasError, setHasError] = useState(false);

  useEffect(() => {
    setHasError(false);
  }, [imageUrl]);

  return (
    <img
      src={!imageUrl || hasError ? IMAGE_FALLBACK_SRC : imageUrl}
      alt={name}
      className={`${className} rounded border border-zinc-200 bg-zinc-100 ${
        fit === "contain" ? "object-contain" : "object-cover"
      }`}
      onError={() => setHasError(true)}
    />
  );
}

function ProductFormModal({
  title,
  description,
  form,
  isProcessing,
  errors,
  modalRef,
  onChange,
  onImageFileChange,
  onClose,
  onSubmit,
}: {
  title: string;
  description: string;
  form: ProductFormState;
  isProcessing: boolean;
  errors: ProductFormErrors;
  modalRef: React.RefObject<HTMLElement | null>;
  onChange: (form: ProductFormState) => void;
  onImageFileChange: (file: File | null) => void;
  onClose: () => void;
  onSubmit: () => void;
}) {
  const [isImageDragging, setIsImageDragging] = useState(false);
  const updateField = (field: keyof ProductFormState, value: string) => {
    onChange({
      ...form,
      [field]: value,
    });
  };

  const handleImageDrop = (event: React.DragEvent<HTMLLabelElement>) => {
    event.preventDefault();
    setIsImageDragging(false);
    onImageFileChange(event.dataTransfer.files?.[0] ?? null);
  };

  return (
    <div
      className="fixed inset-0 z-[100] flex items-center justify-center overflow-hidden bg-zinc-950/45 p-4"
      role="presentation"
      onMouseDown={onClose}
    >
      <section
        ref={modalRef}
        aria-modal="true"
        role="dialog"
        className="flex w-full max-w-2xl flex-col overflow-hidden rounded border border-zinc-200 bg-white shadow-2xl"
        style={{ height: "min(860px, calc(100vh - 2rem))" }}
        onMouseDown={(event) => event.stopPropagation()}
      >
        <header className="flex shrink-0 items-start justify-between border-b border-zinc-200 px-6 py-5">
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

        <div className="min-h-0 flex-1 overflow-y-auto overscroll-contain px-6 py-5">
          <div className="flex flex-col gap-4">
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <label className="flex flex-col gap-2 text-sm font-semibold">
                상품명
                {errors.name && (
                  <span className="text-xs font-medium text-red-600">
                    {errors.name}
                  </span>
                )}
                <input
                  data-product-field="name"
                  className={`h-10 rounded border px-3 text-sm font-normal outline-none focus:border-zinc-950 ${
                    errors.name ? "border-red-400" : "border-zinc-300"
                  }`}
                  placeholder="예) 에티오피아 예가체프"
                  value={form.name}
                  onChange={(event) => updateField("name", event.target.value)}
                />
              </label>
              <label className="flex flex-col gap-2 text-sm font-semibold">
                가격 (원)
                {errors.price && (
                  <span className="text-xs font-medium text-red-600">
                    {errors.price}
                  </span>
                )}
                <input
                  data-product-field="price"
                  className={`h-10 rounded border px-3 text-sm font-normal outline-none focus:border-zinc-950 ${
                    errors.price ? "border-red-400" : "border-zinc-300"
                  }`}
                  inputMode="numeric"
                  placeholder="예) 32000"
                  value={form.price}
                  onChange={(event) => updateField("price", event.target.value)}
                />
              </label>
            </div>

            <label className="flex flex-col gap-2 text-sm font-semibold">
              상품 설명
              {errors.description && (
                <span className="text-xs font-medium text-red-600">
                  {errors.description}
                </span>
              )}
              <textarea
                data-product-field="description"
                className={`h-24 resize-none rounded border px-3 py-3 text-sm font-normal outline-none focus:border-zinc-950 ${
                  errors.description ? "border-red-400" : "border-zinc-300"
                }`}
                placeholder="상품에 대한 간략한 설명을 입력하세요..."
                value={form.description}
                onChange={(event) =>
                  updateField("description", event.target.value)
                }
              />
            </label>

            <div className="flex flex-col gap-2 text-sm font-semibold">
              상품 이미지
              {errors.image && (
                <span className="text-xs font-medium text-red-600">
                  {errors.image}
                </span>
              )}
              <label
                className={`mx-auto grid aspect-square w-full max-w-80 cursor-pointer place-items-center overflow-hidden rounded border border-dashed text-sm transition ${
                  errors.image
                    ? "border-red-400"
                    : isImageDragging
                      ? "border-zinc-950 bg-zinc-100"
                      : "border-zinc-300 bg-zinc-50 hover:bg-zinc-100"
                }`}
                onDragEnter={(event) => {
                  event.preventDefault();
                  setIsImageDragging(true);
                }}
                onDragOver={(event) => {
                  event.preventDefault();
                  setIsImageDragging(true);
                }}
                onDragLeave={() => setIsImageDragging(false)}
                onDrop={handleImageDrop}
              >
                <input
                  data-product-field="image"
                  className="sr-only"
                  type="file"
                  accept="image/jpeg,image/png,image/webp,image/gif"
                  onChange={(event) =>
                    onImageFileChange(event.target.files?.[0] ?? null)
                  }
                />
                {form.imagePreviewUrl || form.imageUrl ? (
                  <ProductImage
                    imageUrl={form.imagePreviewUrl || form.imageUrl}
                    name="이미지 미리보기"
                    className="h-full w-full"
                    fit="contain"
                  />
                ) : (
                  <span className="px-4 text-center font-normal text-zinc-500">
                    이미지를 드래그하거나 클릭해서 선택하세요.
                  </span>
                )}
              </label>
              <span className="text-xs font-normal text-zinc-500">
                jpg, png, webp, gif 파일을 업로드할 수 있습니다.
              </span>
            </div>
          </div>
        </div>

        <footer className="flex shrink-0 justify-end gap-2 border-t border-zinc-200 bg-white px-6 py-4">
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
  const [formErrors, setFormErrors] = useState<ProductFormErrors>({});
  const [editingProduct, setEditingProduct] =
    useState<AdminProductDetailResponse | null>(null);
  const [deletingProduct, setDeletingProduct] =
    useState<AdminProductDetailResponse | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isProcessing, setIsProcessing] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const formModalRef = useRef<HTMLElement | null>(null);

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
    setFormErrors({});
    setIsFormOpen(true);
  };

  const openEditModal = (product: AdminProductDetailResponse) => {
    setEditingProduct(product);
    setForm({
      name: product.name,
      price: String(product.price),
      description: product.description ?? "",
      imageUrl: product.imageUrl ?? "",
      imageFile: null,
      imagePreviewUrl: product.imageUrl ?? "",
    });
    setFormErrors({});
    setIsFormOpen(true);
  };

  const closeFormModal = () => {
    setIsFormOpen(false);
    setEditingProduct(null);
    setForm(emptyForm);
    setFormErrors({});
  };

  const changeImageFile = (file: File | null) => {
    if (file && !ALLOWED_IMAGE_TYPES.has(file.type)) {
      setFormErrors((current) => ({
        ...current,
        image: "jpg, png, webp, gif 이미지만 선택할 수 있습니다.",
      }));
      return;
    }

    setFormErrors((current) => ({
      ...current,
      image: undefined,
    }));
    setForm((current) => ({
      ...current,
      imageFile: file,
      imagePreviewUrl: file ? URL.createObjectURL(file) : current.imageUrl,
    }));
  };

  const changeForm = (nextForm: ProductFormState) => {
    const changedFields = (Object.keys(nextForm) as Array<keyof ProductFormState>)
      .filter((field) => nextForm[field] !== form[field]);

    if (changedFields.length > 0) {
      setFormErrors((current) => {
        const nextErrors = { ...current };

        changedFields.forEach((field) => {
          if (field === "name") nextErrors.name = undefined;
          if (field === "price") nextErrors.price = undefined;
          if (field === "description") nextErrors.description = undefined;
        });

        return nextErrors;
      });
    }

    setForm(nextForm);
  };

  const focusFormField = (field: keyof ProductFormErrors) => {
    window.requestAnimationFrame(() => {
      const target = formModalRef.current?.querySelector<HTMLElement>(
        `[data-product-field="${field}"]`,
      );

      target?.focus();
      target?.scrollIntoView({
        behavior: "smooth",
        block: "center",
      });
    });
  };

  const validateForm = (request: AdminProductRequest): ProductFormErrors => {
    const nextErrors: ProductFormErrors = {};
    const hasImage = Boolean(request.imageUrl || form.imageFile);

    if (!request.name) {
      nextErrors.name = "상품명을 입력해주세요.";
    }

    if (Number.isNaN(request.price) || request.price < 0) {
      nextErrors.price = "0원 이상의 가격을 입력해주세요.";
    }

    if (!request.description) {
      nextErrors.description = "상품 설명을 입력해주세요.";
    }

    if (!hasImage) {
      nextErrors.image = "상품 이미지를 선택해주세요.";
    }

    return nextErrors;
  };

  const submitProduct = async () => {
    const baseRequest = toRequest(form);
    const nextErrors = validateForm(baseRequest);
    const firstErrorField = Object.keys(nextErrors)[0] as
      | keyof ProductFormErrors
      | undefined;

    if (firstErrorField) {
      setFormErrors(nextErrors);
      focusFormField(firstErrorField);
      return;
    }

    setIsProcessing(true);
    setErrorMessage("");
    setFormErrors({});

    try {
      let imageUrl = baseRequest.imageUrl;

      if (form.imageFile) {
        const uploadedImage = await uploadAdminProductImage(form.imageFile);
        imageUrl = uploadedImage.imageUrl;
      }

      const request = {
        ...baseRequest,
        imageUrl,
      };

      if (editingProduct) {
        await updateAdminProduct(editingProduct.id, request);
      } else {
        await createAdminProduct(request);
      }

      closeFormModal();
      window.location.reload();
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
          errors={formErrors}
          modalRef={formModalRef}
          onChange={changeForm}
          onImageFileChange={changeImageFile}
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
