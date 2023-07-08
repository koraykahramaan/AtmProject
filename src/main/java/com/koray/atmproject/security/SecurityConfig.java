package com.koray.atmproject.security;

import com.koray.atmproject.service.UserInfoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public UserDetailsService userDetailsService() {

        return new UserInfoUserDetailsService();

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

         http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/accounts","/api/v1/users","/api/v1/users/**","/api/v1/users/authenticate").permitAll()
                .and().authorizeHttpRequests().requestMatchers(antMatcher("/h2-console/**")).permitAll()
                .and().authorizeHttpRequests().requestMatchers(antMatcher("/api/v1/users/authenticate")).permitAll()
                .and().authorizeHttpRequests().requestMatchers("/api/v1/accounts/**").authenticated()
//                .and().authorizeHttpRequests().requestMatchers("/api/v1/accounts/transfer/**").authenticated()
                .and().csrf().ignoringRequestMatchers(antMatcher("/h2-console/**")).ignoringRequestMatchers("/api/v1/users/new","/api/v1/users/authenticate","/api/v1/accounts/new","/api/v1/accounts/transfer")
                .and().headers().frameOptions().disable();
         return http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers(new AntPathRequestMatcher("/h2-console","/h2-console/**"));
//        return (web) -> web.ignoring().requestMatchers("/h2-console/**");
//    }
}
