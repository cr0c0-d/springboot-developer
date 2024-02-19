package me.croco.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.croco.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userDetailService;

    // 1. 스프링 시큐리티 기능 비활성화 -> 인증/인가를 모든 곳에 적용하지는 않음
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }

    // 2. 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()    // 3. 인증, 인가 설정
                .requestMatchers("/login", "/signup", "/user").permitAll()
                .anyRequest().authenticated()   // 위 설정 경로 이외의 요청 -> 인가는 필요x 인증은 필요o
                .and()
                .formLogin()    // 4. 폼 기반 로그인 설정
                .loginPage("/login")    // 로그인 페이지 경로
                .defaultSuccessUrl("/articles") // 로그인 성공시 이동 경로
                .and()
                .logout()   // 5. 로그아웃 설정
                .logoutSuccessUrl("/login") // 로그아웃 성공시 이동 경로
                .invalidateHttpSession(true)    // 로그아웃 후 세션 전체 삭제 여부
                .and()
                .csrf().disable()   // 6. csrf 비활성화 -> CSRF 공격 방지 위해 활성화해야 하지만 실습 위해 비활성화
                .build();
    }

    // 7. 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailService userDetailService)
        throws Exception {

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)  // 8. 사용자 정보 서비스 설정
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    // 9. 패스워드 인코더로 사용할 빈 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
