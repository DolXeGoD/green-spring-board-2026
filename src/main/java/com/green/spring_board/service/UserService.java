package com.green.spring_board.service;

import com.green.spring_board.dto.*;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public void signup(SignupRequest signupRequest) {
        // 아이디가 사용 중인지 확인
        if( userRepository.existsByUsername(signupRequest.getUsername()) ){
            throw new ResourceConflictException("Username already exists");
        }

        // 이메일이 사용 중인지 확인
        if( userRepository.existsByEmail(signupRequest.getEmail()) ){
            throw new ResourceConflictException("Email already exists");
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // db save
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setNickname(signupRequest.getNickname());
        user.setState(UserState.ACTIVE);
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()
            ));
        } catch (AuthenticationException e) {
            throw new UnauthenticatedException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        // 1. 이메일 존재하는건지 확인
        User user = userRepository.findByUsername(loginRequest.getUsername());

        if(user == null){
            throw new ResourceNotFoundException("User not found");
        }

        if(user.getState() == UserState.QUITTED){
            throw new ResourceNotFoundException("탈퇴된 회원입니다.");
        }

        // 액세스 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(user.getUsername());
        // 3. 로그인 성공
        return new LoginResponse(accessToken);
    }

    public MyInfoResponse getUserInfo(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        // 4. DB에서 이 유저의 닉네임과 이메일을 받아옴
        String email = user.getEmail();
        String nickname = user.getNickname();

        // 5. 돌려줌.
        MyInfoResponse myInfoResponse = new MyInfoResponse();
        myInfoResponse.setEmail(email);
        myInfoResponse.setNickname(nickname);

        return myInfoResponse;
    }

    public void updateUserInfo(UserUpdateRequest userUpdateRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        if(userUpdateRequest.getEmail()!=null
                && !userUpdateRequest.getEmail().isBlank()
                && !userUpdateRequest.getEmail().equals(user.getEmail())
        ){
            user.setEmail(userUpdateRequest.getEmail());
        }

        if(userUpdateRequest.getNickname()!=null
                && !userUpdateRequest.getNickname().isBlank()
        ) {
            user.setNickname(userUpdateRequest.getNickname());
        }
        userRepository.save(user);
    }

    public void deleteUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        user.setState(UserState.QUITTED);
        userRepository.save(user);
        // TODO :: 리프레시 토큰 삭제
    }
}
