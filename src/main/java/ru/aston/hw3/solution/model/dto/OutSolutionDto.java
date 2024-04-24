package ru.aston.hw3.solution.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.aston.hw3.developer.model.dto.OutDeveloperDto;
import ru.aston.hw3.owner.model.dto.OutOwnerDto;

import java.util.List;

@Data
@Builder
public class OutSolutionDto {
    private Long id;
    private String name;
    private String version;
    private OutOwnerDto owner;
    private List<OutDeveloperDto> developers;
}
