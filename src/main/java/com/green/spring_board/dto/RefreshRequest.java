package com.green.spring_board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {
    @NotBlank
    private String refreshToken;
}
