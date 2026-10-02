package com.green.spring_board.service;

import com.green.spring_board.dto.LoginRequest;
import com.green.spring_board.dto.SignupRequest;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.exceptions.UserRequestException;
import com.green.spring_board.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder
            = new BCryptPasswordEncoder();

    public void signup(SignupRequest signupRequest) {
        // 이메일과 비밀번호가 공백이 아닌지 확인
        if(signupRequest.getEmail().isBlank()
                || signupRequest.getPassword().isBlank()){
            throw new UserRequestException("Email or password cannot be blank");
        }

        // 이메일이 사용 중인지 확인
        if( userRepository.existsByEmail(signupRequest.getEmail()) ){
            throw new ResourceConflictException("Email already exists");
        }

        // 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(
                signupRequest.getPassword()
        );

        // db save
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashedPassword);
        user.setNickname(signupRequest.getNickname());
        userRepository.save(user);
    }

    public int login(LoginRequest loginRequest) {
        // 1. 이메일 존재하는건지 확인
        Optional<User> userOptional
                = userRepository.findByEmail(loginRequest.getEmail());

        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();
        // 2. 비밀번호가 올바른지 확인
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new UnauthenticatedException("Wrong password");
        }

        // 3. 로그인 성공
        return user.getId();
    }
}
