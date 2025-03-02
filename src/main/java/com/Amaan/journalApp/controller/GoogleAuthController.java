package com.Amaan.journalApp.controller;

import com.Amaan.journalApp.entity.User;
import com.Amaan.journalApp.repository.UserRepository;
import com.Amaan.journalApp.service.UserDetailsServiceImpl;
import com.Amaan.journalApp.utilis.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    //    @GetMapping("/callback")
//    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code){
//        try {
//            // 1. Exchange auth code for tokens
//            String tokenEndpoint = "https://oauth2.googleapis.com/token";
//
//            Map<String,String> params = new HashMap<>();
//            params.put("code",code);
//            params.put("client_id",clientId);
//            params.put("client_secret",clientSecret);
//            params.put("redirect_uri","https://developers.google.com/oauthplayground");
//            params.put("grant_type","authorization_code");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            HttpEntity<Map<String,String>> request = new HttpEntity<>(params,headers);
//
//            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
//            String idToken = (String) tokenResponse.getBody().get("id_token");
//            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
//            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
//            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
//                Map<String, Object> userInfo = userInfoResponse.getBody();
//                String email = (String) userInfo.get("email");
//                UserDetails userDetails = null;
//                try{
//                    userDetails = userDetailsService.loadUserByUsername(email);
//                }catch (Exception e){
//                    User user = new User();
//                    user.setEmail(email);
//                    user.setUserName(email);
//                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
//                    user.setRoles(Arrays.asList("USER"));
//                    userRepository.save(user);
//                }
//                String jwtToken = jwtUtil.generateToken(email);
//                return ResponseEntity.ok(Collections.singletonMap("token", jwtToken));
//            }
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//        } catch (Exception e) {
//            log.error("");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        try {
            // 1. Exchange auth code for tokens
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", "http://localhost:8080/journalApp/auth/google/callback"); // Ensure this is correct
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            log.info("Exchanging auth code for token. Code: {}", code);
            log.info("Client ID: {}", clientId);
            log.info("Redirect URI: {}", "http://localhost:8080/journalApp/auth/google/callback");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> tokenResponse = null; // Declare outside the try block

            try {
                tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            } catch (HttpClientErrorException e) {
                log.error("Error while calling Google OAuth token API. Response Body: {}", e.getResponseBodyAsString());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve access token.");
            }

            log.info("Token response: {}", tokenResponse.getBody());

            // Ensure the response contains "id_token"
            Map<String, Object> responseBody = tokenResponse.getBody();
            if (responseBody == null || !responseBody.containsKey("id_token")) {
                log.error("id_token missing in response: " + responseBody);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve ID token from Google.");
            }

            String idToken = (String) responseBody.get("id_token");
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");

                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                } catch (Exception e) {
                    User user = new User();
                    user.setEmail(email);
                    user.setUserName(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRoles(Arrays.asList("USER"));
                    userRepository.save(user);

                    // Reload user details after saving
                    userDetails = userDetailsService.loadUserByUsername(email);
                }

                String jwtToken = jwtUtil.generateToken(email);
                return ResponseEntity.ok(Collections.singletonMap("token", jwtToken));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            log.error("Error in Google OAuth callback: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
