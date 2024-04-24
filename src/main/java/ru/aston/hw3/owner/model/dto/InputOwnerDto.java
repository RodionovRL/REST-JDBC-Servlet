package ru.aston.hw3.owner.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InputOwnerDto {
    private String lastName;
    private String firstName;
    private String email;
    private List<Long> solutionIds;
}
