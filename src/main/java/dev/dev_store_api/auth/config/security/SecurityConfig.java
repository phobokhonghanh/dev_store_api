package dev.dev_store_api.auth.config.security;

import dev.dev_store_api.auth.config.routes.AuthRoutes;
import dev.dev_store_api.common.config.properties.ApiProperties;
import dev.dev_store_api.common.config.properties.CorsProperties;
import dev.dev_store_api.common.filter.RequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ApiProperties apiProperties;
    private final RequestLoggingFilter requestLoggingFilter;
    private final CorsProperties corsProps;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, ApiProperties apiProperties, RequestLoggingFilter requestLoggingFilter, CorsProperties corsProps) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.apiProperties = apiProperties;
        this.requestLoggingFilter = requestLoggingFilter;
        this.corsProps = corsProps;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(corsProps.getAllowCredentials());
        config.setAllowedOriginPatterns(corsProps.getAllowedOriginPatterns());
        config.setAllowedMethods(corsProps.getAllowedMethods());
        config.setAllowedHeaders(corsProps.getAllowedHeaders());
        config.setExposedHeaders(corsProps.getExposedHeaders());
        config.setMaxAge(corsProps.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final String contextPath = apiProperties.context();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                contextPath + AuthRoutes.PREFIX + AuthRoutes.LOGIN,
                                contextPath + AuthRoutes.PREFIX + AuthRoutes.REGISTER + "/**",
                                contextPath + AuthRoutes.PREFIX + AuthRoutes.VERIFY_OTP.split("/\\{")[0] + "/**",
                                contextPath + AuthRoutes.PREFIX + AuthRoutes.REFRESH_OTP,
                                contextPath + AuthRoutes.PREFIX + AuthRoutes.REFRESH_TOKEN,
                                contextPath + AuthRoutes.PREFIX + AuthRoutes.REGISTRATION_STATUS
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(requestLoggingFilter, CsrfFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
