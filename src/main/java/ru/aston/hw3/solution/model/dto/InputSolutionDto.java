package ru.aston.hw3.solution.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InputSolutionDto {
    private String name;
    private String version;
    private Long ownerId;
}
