package com.Amaan.journalApp.config;

import com.Amaan.journalApp.filter.JwtFilter;
import com.Amaan.journalApp.service.UserDetailsServiceImpl;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
// @Profile("dev") //if the application is using appliaction-dev properties
public class SpringSecurity {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injecting password encoder


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

//        return http.authorizeHttpRequests(request -> request
//                        .requestMatchers("/public/**", "/api/public/**", "/api/public/authenticate").permitAll() // Publicly accessible
//                        .requestMatchers("/journal/**", "/user/**").authenticated() // Requires authentication
//                        .requestMatchers("/admin/**").hasRole("ADMIN") // Requires ADMIN role
//                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
//                        .anyRequest().authenticated()) // All other requests  require authentication
//                .httpBasic(Customizer.withDefaults()) // Enables HTTP Basic Authentication
//                .formLogin(Customizer.withDefaults()) // Enables default login form
//                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF (use cautiously)
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();



        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API access
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()  // ✅ Allow Swagger without authentication
                        .requestMatchers("/","/public/**", "/api/public/**", "/api/public/authenticate").permitAll() // Publicly accessible
                        .requestMatchers("/journal/**", "/user/**").authenticated() // Requires authentication
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Requires ADMIN role
                        .requestMatchers("/auth/google/**").permitAll() // ✅ Allow Google OAuth
                        .anyRequest().authenticated() // 🔒 Secure all other endpoints

                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions
                .httpBasic(httpBasic -> httpBasic.disable()) // Disable basic auth
                .formLogin(formLogin -> formLogin.disable()) // Disable form login
                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/home", true)) // Redirect after successful login
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // 🔥 Add JWT Filter

        return http.build();



    }



    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

//    @Bean
//    @Lazy // 🔄 Delays initialization to prevent circular dependency
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

}
