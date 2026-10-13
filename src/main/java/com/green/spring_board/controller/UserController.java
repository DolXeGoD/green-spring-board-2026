package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.service.BoardService;
import com.green.spring_board.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "그린 커뮤니티 회원 API", description = "회원 관련 API 모음입니다.")
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final BoardService boardService;

    @Operation(summary = "회원가입 API", description = "회원가입을 할 때 씀")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        userService.signup(signupRequest);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
    ){
        LoginResponse result = userService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
        @RequestHeader("Authorization") String authorization
    ){
        String accessToken = authorization.substring(7);
        userService.logout(accessToken);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(
            @Valid @RequestBody RefreshRequest refreshRequest
    ) {
        // 리프레시 토큰을 이용해 새로운 액세스 토큰을 재발급 받는 API
        RefreshResponse response = userService.refresh(refreshRequest);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getCurrentUser(
    ){
        MyInfoResponse response = userService.getUserInfo();

        return ResponseEntity.ok().body(ApiResponse.ok(response));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            @Valid @RequestBody UserUpdateRequest userUpdateRequest

    ){
        userService.updateUserInfo(userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 유저 탈퇴 기능
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            HttpServletRequest request
    ){
        // 1. DB 삭제
        userService.deleteUser();

        return ResponseEntity.ok(ApiResponse.ok());
    }
}
