package ru.aston.hw3.developer.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InputDeveloperDto {
    private String lastName;
    private String firstName;
    private String email;
}
