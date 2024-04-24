package ru.aston.hw3.owner.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.aston.hw3.owner.model.Owner;
import ru.aston.hw3.owner.model.dto.InputOwnerDto;
import ru.aston.hw3.owner.model.dto.OutOwnerDto;

import java.util.Collection;
import java.util.List;

@Mapper()
public interface OwnerMapper {
    OwnerMapper INSTANCE = Mappers.getMapper(OwnerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "solutions", ignore = true)
    Owner toOwner(InputOwnerDto inputOwnerDto);

    OutOwnerDto toOutOwnerDto(Owner owner);
}
