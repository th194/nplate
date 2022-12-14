package com.netive.nplate.configuration;

import com.netive.nplate.component.LoginFailHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/admin/member/delete/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/admin/member/changeRole/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/admin/**").access("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
                .anyRequest().permitAll();

        http.formLogin()
                .loginPage("/")
                .usernameParameter("id")
                .passwordParameter("pwd")
                .loginProcessingUrl("/loginProc")
                .defaultSuccessUrl("/login/success")
                .failureHandler(loginFailHandler())
                .permitAll();

        // 스프링 시큐리티 적용 시 스마트에디터 로드 불가 >> 해결
        http.headers().frameOptions().sameOrigin();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginFailHandler loginFailHandler() {
        return new LoginFailHandler();
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();
    }
}
