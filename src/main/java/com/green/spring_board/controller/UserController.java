package com.green.spring_board.controller;

import com.green.spring_board.dto.*;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.service.BoardService;
import com.green.spring_board.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest
    ){
        int userId = userService.login(loginRequest);
        HttpSession session = httpServletRequest.getSession();
        httpServletRequest.changeSessionId();
        session.setAttribute("userId", userId);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request
    ){
        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getCurrentUser(
            HttpServletRequest httpServletRequest
    ){
        // 1. 이 사람의 세션을 가져옴
        HttpSession session = httpServletRequest.getSession(false);

        if(session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }

        // 2. 세션에서 유저 아이디 뽑아옴
        int userId = (int) session.getAttribute("userId");
        MyInfoResponse response = userService.getUserInfo(userId);

        return ResponseEntity.ok().body(ApiResponse.ok(response));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            HttpServletRequest request,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest

    ){
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        int userId = (int) session.getAttribute("userId");
        userService.updateUserInfo(userId, userUpdateRequest);
        return ResponseEntity.ok(ApiResponse.ok());
    }

    // 유저 탈퇴 기능
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            HttpServletRequest request
    ){
        HttpSession session = request.getSession(false);
        if(session == null || session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("로그인이 필요합니다.");
        }
        int userId = (int) session.getAttribute("userId");

        // 1. DB 삭제
        userService.deleteUser(userId);
        // 2. 세션 비활성화
        session.invalidate();

        return ResponseEntity.ok(ApiResponse.ok());
    }
}
