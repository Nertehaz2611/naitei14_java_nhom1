package org.example.foodanddrinkproject.controller.api;


import org.example.foodanddrinkproject.dto.*;
import org.example.foodanddrinkproject.security.CustomUserDetailsService;
import org.example.foodanddrinkproject.security.JwtTokenProvider;
import org.example.foodanddrinkproject.security.UserPrincipal;
import org.example.foodanddrinkproject.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthController(AuthService authService,
                          JwtTokenProvider tokenProvider,
                          CustomUserDetailsService customUserDetailsService) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }


    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.loginUser(loginRequest));
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        return authService.registerUser(signUpRequest);
    }


    @GetMapping("/success")
    public ResponseEntity<LoginSuccessResponse> oauthLoginSuccess(@RequestParam("token") String token) {
        Long userId = tokenProvider.getUserIdFromJWT(token);

        UserPrincipal userPrincipal = (UserPrincipal) customUserDetailsService.loadUserById(userId);

        UserSummaryDto userSummary = new UserSummaryDto(
                userPrincipal.getId(),
                userPrincipal.getUsername(), // This returns email
                userPrincipal.getFullName()
        );

        JwtAuthResponse tokenResponse = new JwtAuthResponse(token);

        return ResponseEntity.ok(new LoginSuccessResponse(tokenResponse, userSummary));
    }
}
