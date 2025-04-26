package com.vaibhav.booking.system.security.config;


import com.vaibhav.booking.system.serviceimpl.UserService;
import com.vaibhav.booking.system.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private  UserService userService;
   // private  MfaAuthenticationFilter mfaAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SecurityConfig(UserService userService,
                          JwtAuthenticationFilter jwtAuthenticationFilter){
         //   this.mfaAuthenticationFilter=mfaAuthenticationFilter;
            this.userService=userService;
            this.jwtAuthenticationFilter=jwtAuthenticationFilter;
        }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/mfa/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/seat/book").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/disable").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/booking/**").authenticated()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers("/booking/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
             /*   .httpBasic(httpBasic -> httpBasic
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                        })
                )*/
                //.addFilterAfter(mfaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                .addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-MFA-OTP"));
        configuration.setExposedHeaders(List.of("X-MFA-Required"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false);
        return new ProviderManager(provider);
    }


}
