package org.example.foodanddrinkproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginSuccessResponse {
    private JwtAuthResponse token;
    private UserSummaryDto user;
}