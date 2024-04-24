package ru.aston.hw3.solution.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InputSolutionLinkDto {
    private Long solutionId;
    private Long ownerId;
    private Long developerId;
}
