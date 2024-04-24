package ru.aston.hw3.owner.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.aston.hw3.solution.model.dto.OutSolutionDto;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class OutOwnerDto {
    private long id;
    private String lastName;
    private String firstName;
    private String email;
    @Builder.Default
    private List<OutSolutionDto> solutions = Collections.emptyList();
}
