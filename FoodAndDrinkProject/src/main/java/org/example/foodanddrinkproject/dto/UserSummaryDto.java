package org.example.foodanddrinkproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserSummaryDto {
    private Long id;
    private String email;
    private String fullName;
}
