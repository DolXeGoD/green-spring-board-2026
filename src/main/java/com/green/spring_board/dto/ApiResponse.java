package com.green.spring_board.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // 생성자로 만들지 않고 굳이 ok, fail 등 정적 팩토리 메서드를 사용하는 이유
    // - 사용하는 곳에서는 해당 클래스의 내부 구조를 몰라도 된다.
    // - 생성자를 사용하는 경우, ApiResponse 구조가 수정되면 해당 클래스의 생성자 호출부 코드를 모두 바꿔주어야 한다.

    // 성공 (데이터 O)
    public static <T> ApiResponse<T> ok(T data) {
        // 빌더 패턴의 장점
        // 생성자 오버로딩이 필요없다 (줄일 수 있다)
        // 객체 생성 코드만 봐도 어느 필드에 뭐가 들어가는지 알 수 있다. (가독성이 좋다)
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    // 성공 (데이터 X)
    public static <T> ApiResponse<T> ok() {
        return ApiResponse.<T>builder()
                .success(true)
                .build();
    }

    // 실패
    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
