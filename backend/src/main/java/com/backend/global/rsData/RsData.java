package com.backend.global.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RsData<T> {

    private String resultCode;  // "200", "400", "404", "500"
    private String message;     // 응답 메시지
    private T data;             // 실제 응답 데이터

    // 데이터 없는 경우 (삭제, 에러 응답 등)
    public static <T> RsData<T> of(String resultCode, String message) {
        return new RsData<>(resultCode, message, null);
    }

    // 데이터 있는 경우 (조회, 등록, 수정 응답 등)
    public static <T> RsData<T> of(String resultCode, String message, T data) {
        return new RsData<>(resultCode, message, data);
    }

    // 성공 여부 확인
    public boolean isSuccess() {
        return resultCode.startsWith("2");
    }
}
