package com.backend.domain.order.order.entity;

public enum OrderStatus{
    ORDERED,
    // 주문완료
    // 관리자가 아직 확인하지 않은 상태
    // 사용자가 주문 수정/취소 가능

    CONFIRMED,
    // 주문확인
    // 관리자가 주문을 확인한 상태
    // 사용자 직접 수정/취소 불가

    PREPARING_SHIPMENT,
    // 배송준비중
    // 포장 또는 출고 준비가 시작된 상태
    // 사용자 직접 수정/취소 불가

    SHIPPED,
    // 배송중
    // 상품이 배송사에 전달된 상태
    // 사용자 직접 수정/취소 불가

    DELIVERED,
    // 배송완료
    // 상품이 배송 완료된 상태

    CANCELED
    // 주문취소
    // 주문이 취소된 상태
}
