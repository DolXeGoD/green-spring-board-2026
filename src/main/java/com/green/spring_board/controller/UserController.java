package com.green.spring_board.controller;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.MyInfoResponse;
import com.green.spring_board.dto.SignupRequest;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        try{
            userService.signup(signupRequest);
            return ResponseEntity.ok().build();
        } catch (ResourceConflictException e){
            return ResponseEntity.status(409).build();
        } catch (UserRequestException e){
            return ResponseEntity.badRequest().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest httpServletRequest
    ){
        try{
            int userId = userService.login(loginRequest);
            HttpSession session = httpServletRequest.getSession();
            httpServletRequest.changeSessionId();
            session.setAttribute("userId", userId);
            return ResponseEntity.ok().build();

        }catch (ResourceNotFoundException e){
            return ResponseEntity.notFound().build();
        }catch (UnauthenticatedException e){
            return ResponseEntity.status(401).build();
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<MyInfoResponse> getCurrentUser(
            HttpServletRequest httpServletRequest
    ){
        // 1. 이 사람의 세션을 가져옴
        HttpSession session = httpServletRequest.getSession(false);

        if(session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).build();
        }

        // 2. 세션에서 유저 아이디 뽑아옴

        // 3. 유저 아이디로 DB 조회함
        // 4. DB에서 이 유저의 닉네임과 이메일을 받아옴
        // 5. 돌려줌.
    }
}
