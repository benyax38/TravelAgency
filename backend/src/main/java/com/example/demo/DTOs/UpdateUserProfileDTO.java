package com.example.demo.DTOs;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileDTO {

    @Size(max = 20)
    private String phone;

    @Size(max = 20)
    private String dni;

    @Size(max = 20)
    private String nationality;
}
