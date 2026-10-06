package com.green.spring_board.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardUpdateRequest {
    // Board 수정의 경우
    // 수정하려는 필드 값만 요청에 담아보낸다.
    // NotBlank를 붙이면 수정(Patch) API 용도와 다르게
    // 모든 필드를 다 채워줘야 하는 문제가 발생한다.
    @Size(min = 10, max = 50) // 10자 이상, 50자 이하
    private String title;

    @Size(min = 10) // 10자 이상
    private String content;
}
