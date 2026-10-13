package com.green.spring_board.service;

import com.green.spring_board.dto.*;
import com.green.spring_board.entity.AccessTokenBlacklist;
import com.green.spring_board.entity.RefreshToken;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceConflictException;
import com.green.spring_board.exceptions.UnauthenticatedException;
import com.green.spring_board.global.UserState;
import com.green.spring_board.repository.AccessTokenBlacklistRepository;
import com.green.spring_board.repository.RefreshTokenRepository;
import com.green.spring_board.repository.UserRepository;
import com.green.spring_board.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    @Transactional
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

    @Transactional
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

        // 액세스 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

        // 1. 해당 유저의 기존 리프레시 토큰(들) 삭제
        // TODO :: 현재 deleteByUserId라는 유저 아이디를 기준으로 리프레시 토큰을 삭제해주는 기능이 있지만,
        //  해당 기능(JPA 파생 삭제 메서드)을 사용하려면 Transaction이 필요해 임시 우회
        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(user.getId());
        refreshTokenRepository.deleteAll(tokens);

        // 2. 해당 유저의 신규 리프레시 토큰 저장
        Date expiration = tokenProvider.getExpiration(refreshToken);
        LocalDateTime expirationDateTime =
                expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        RefreshToken refresh = new RefreshToken();
        refresh.setUser(user);
        refresh.setToken(refreshToken);
        refresh.setExpirationDateTime(expirationDateTime);
        refreshTokenRepository.save(refresh);
        // 3. 로그인 성공
        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String accessToken) {
        // 1. 해당 사용자의 리프레시 토큰 무효화
        // - DB에서 해당 사용자의 리프레시 토큰을 지운다.
        String username = SecurityContextHolder
                .getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(user.getId());
        refreshTokenRepository.deleteAll(tokens);

        // 2. 해당 사용자의 액세스 토큰 무효화
        // - DB 내 블랙리스트 테이블에 해당 토큰을 추가한다.
        // - 만료 이후에는 블랙리스트 해제가 필요하므로 만료일자도 함께 넣는다.
        Date expiration = tokenProvider.getExpiration(accessToken);
        LocalDateTime convertedDateTime = expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        AccessTokenBlacklist accessTokenBlacklist = new AccessTokenBlacklist();
        accessTokenBlacklist.setToken(accessToken);
        accessTokenBlacklist.setExpirationDateTime(convertedDateTime);
        accessTokenBlacklistRepository.save(accessTokenBlacklist);
    }

    @Transactional(readOnly = true)
    public RefreshResponse refresh(@Valid RefreshRequest refreshRequest) {
        String userRefreshToken = refreshRequest.getRefreshToken();
        // 1. 리프레시 토큰 위/변조 여부 검증
        if(!tokenProvider.validateRefreshToken(userRefreshToken)) {
            // 검증 실패 예외처리
            throw new UnauthenticatedException("만료되었거나 유효하지 않은 리프레시 토큰입니다.");
        }

        // 2. DB 내 존재하는 리프레시 토큰인지 검증
        Optional<RefreshToken> optionalRT = refreshTokenRepository.findByToken(userRefreshToken);
        if (optionalRT.isEmpty()) {
            throw new UnauthenticatedException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 3. 해당 리프레시 토큰이 만료되진 않았는지 검증
        // ! + isAfter = 만료 시간이 현재보다 이전이거나 같은 경우 (= 현재보다 미래가 아닌 경우)
        // isBefore = 만료 시간이 현재보다 이전인 경우
        // 만료 시각과 현재 시각이 정확히 같을 때도 만료된 것으로 처리
        if (!optionalRT.get().getExpirationDateTime().isAfter(LocalDateTime.now())) {
            throw new UnauthenticatedException("만료된 않은 리프레시 토큰입니다.");
        }

        // 4. 토큰 내 유저네임(subject)의 유저 정보가 유효한지 검증
        // 해당 유저가 존재하는지, 활성화 상태인지(탈퇴하지는 않았는지) 등등
        String username = tokenProvider.getUsernameFromToken(userRefreshToken);
        User user = userRepository.findByUsername(username);
        if (user == null || user.getState() != UserState.ACTIVE) {
            throw new UnauthenticatedException("인증할 수 없는 계정입니다.");
        }

        // 5. 최종적으로 새 액세스 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(username);
        return new RefreshResponse(accessToken);
    }

    @Transactional(readOnly = true)
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

    @Transactional
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

    @Transactional
    public void deleteUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        user.setState(UserState.QUITTED);
        userRepository.save(user);
        // TODO :: 리프레시 토큰 삭제
    }
}
