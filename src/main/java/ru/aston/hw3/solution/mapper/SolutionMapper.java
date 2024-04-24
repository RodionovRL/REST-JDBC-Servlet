package ru.aston.hw3.solution.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.aston.hw3.solution.model.Solution;
import ru.aston.hw3.solution.model.dto.InputSolutionDto;
import ru.aston.hw3.solution.model.dto.OutSolutionDto;

import java.util.Collection;
import java.util.List;

@Mapper()
public interface SolutionMapper {
    SolutionMapper INSTANCE = Mappers.getMapper(SolutionMapper.class);

    @Mapping(target = "id", ignore = true)
    Solution toSolution(InputSolutionDto inputSolutionDto);

    OutSolutionDto toOutSolutionDto(Solution solution);

    List<OutSolutionDto> toOutSolutionDtoList(Collection<Solution> solutions);
}
