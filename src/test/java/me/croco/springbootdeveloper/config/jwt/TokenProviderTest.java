package me.croco.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Jwts;
import me.croco.springbootdeveloper.domain.User;
import me.croco.springbootdeveloper.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    // 1. generateToken() 검증 테스트
    // (토큰 생성 메서드 테스트)
    @DisplayName("generateToken() : 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given
        // 토큰에 유저 정보를 추가하기 위한 테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@email.com")
                .password("test")
                .build()
        );

        // when
        // 토큰 제공자의 generateToken()으로 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then
        // jjwt 라이브러리 이용해 토큰 복호화
        // 토큰을 만들 때 클레임으로 넣어둔 id값이 given의 유저 ID와 동일한지 확인
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    // 2. validToken() 검증 테스트
    // 검증 실패 테스트
    @DisplayName("validToken() : 만료된 토큰인 때에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given
        // 토큰 생성, 만료시간을 현재시간 - 7일로 설정해 이미 만료된 토큰으로 생성
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when
        // 유효한 토큰인지 검증 후 결괏값 반환
        boolean result = tokenProvider.validToken(token);

        // then
        // 유효하지 않은 토큰인 것을 확인
        assertThat(result).isFalse();
    }

    // 검증 성공 테스트
    @DisplayName("validToken() : 유효한 토큰인 때에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given
        // 토큰 생성, 만료시간은 기본값인 현재시간 + 14일 뒤
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        // when
        // 유효한 토큰인지 검증 후 결괏값 반환
        boolean result = tokenProvider.validToken(token);

        // then
        // 유효한 토큰인 것을 확인
        assertThat(result).isTrue();
    }

    // 3. getAuthentication() 검증 테스트
    // 토큰을 전달받아, 인증 정보를 담은 객체 Authentication을 반환
    @DisplayName("getAuthentication() : 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given
        // subject는 유저의 이메일
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when
        // 토큰 제공자의 getAuthentication() 호출, 인증 객체 반환
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        assertThat(
                        (
                            (UserDetails) authentication.getPrincipal()
                        ).getUsername()
                    ).isEqualTo(userEmail);
    }

    // 4. getUserId() 검증 테스트
    @DisplayName("getUserId() : 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when
        Long userIdByToken = tokenProvider.getUserId(token);

        assertThat(userIdByToken).isEqualTo(userId);

    }

}
