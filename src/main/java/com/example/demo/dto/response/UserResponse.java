package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import com.example.demo.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private Integer age;
    private Double monthlyIncome;
    private Integer creditScore;
}

