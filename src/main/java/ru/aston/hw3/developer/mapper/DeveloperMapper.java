package ru.aston.hw3.developer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.aston.hw3.developer.model.Developer;
import ru.aston.hw3.developer.model.dto.InputDeveloperDto;
import ru.aston.hw3.developer.model.dto.OutDeveloperDto;

import java.util.Collection;
import java.util.List;

@Mapper()
public interface DeveloperMapper {
    DeveloperMapper INSTANCE = Mappers.getMapper(DeveloperMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "solutions", ignore = true)
    Developer toDeveloper(InputDeveloperDto inputDeveloperDto);

    OutDeveloperDto toOutDeveloperDto(Developer developer);

    List<OutDeveloperDto> toOutDeveloperDtoList(Collection<Developer> developers);
}
