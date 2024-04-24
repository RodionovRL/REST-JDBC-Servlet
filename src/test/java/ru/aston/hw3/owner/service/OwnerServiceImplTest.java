package ru.aston.hw3.owner.service;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hw3.exception.NotFoundException;
import ru.aston.hw3.owner.OwnerRepository;
import ru.aston.hw3.owner.mapper.OwnerMapper;
import ru.aston.hw3.owner.model.Owner;
import ru.aston.hw3.owner.model.dto.InputOwnerDto;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTest {
    @Mock
    private OwnerRepository repository;
    @Mock
    private OwnerMapper ownerMapper;
    @InjectMocks
    private OwnerServiceImpl ownerService;
    HttpServletResponse response = mock(HttpServletResponse.class);
    private final Gson gson = new Gson();
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);

    @Test
    @SneakyThrows
    void addOwner_WhenAllDataIsGood() {

        InputOwnerDto inputOwnerDto = InputOwnerDto.builder()
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        Owner owner = Owner.builder()
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        when(ownerMapper.toOwner(inputOwnerDto)).thenReturn(owner);
        when(repository.addOwner(owner)).thenReturn(Optional.of(owner));
        when(response.getWriter()).thenReturn(writer);

        ownerService.addOwner(inputOwnerDto, response);

        verify(ownerMapper).toOwner(inputOwnerDto);
        verify(repository).addOwner(owner);
        verify(response).getWriter();
        verify(ownerMapper).toOutOwnerDto(any(Owner.class));
        assertTrue(stringWriter.toString().contains(gson.toJson(ownerMapper.toOutOwnerDto(owner))));
    }

    @Test
    @SneakyThrows
    void getOwnerById_WhenAllDataIsGood() {
        var id = 15L;
        var isLazy = true;
        Owner owner = Owner.builder()
                .id(id)
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        when(repository.getOwner(id, isLazy)).thenReturn(Optional.of(owner));
        when(response.getWriter()).thenReturn(writer);

        ownerService.getOwnerById(id, isLazy, response);

        verify(repository).getOwner(id, isLazy);
        verify(response).getWriter();
        verify(ownerMapper).toOutOwnerDto(any(Owner.class));
        assertTrue(stringWriter.toString().contains(gson.toJson(ownerMapper.toOutOwnerDto(owner))));
    }

    @Test
    void getOwnerById_WhenCameEmptyFromRepository_WhenNotFoundException() {
        var id = 15L;
        var isLazy = true;
        when(repository.getOwner(id, isLazy)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> ownerService.getOwnerById(id, isLazy, response)
                , String.format("Пользователь с id=%d не найден", id));
    }

    @Test
    void deleteOwnerById_WhenOwnerFoundAndDeleted_ThenReturnStatus204() {
        var id = 15L;
        var isLazy = true;
        Owner owner = Owner.builder()
                .id(id)
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        when(repository.getOwner(id, isLazy)).thenReturn(Optional.of(owner));
        when(repository.deleteOwner(id)).thenReturn(1);

        ownerService.deleteOwnerById(id, response);

        verify(repository).getOwner(id, isLazy);
        verify(repository).deleteOwner(id);
        verify(response).setStatus(204);
    }

    @Test
    void deleteOwnerById_WhenOwnerFoundButNotDeleted_ThenReturnStatus400() {
        var id = 15L;
        var isLazy = true;
        Owner owner = Owner.builder()
                .id(id)
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        when(repository.getOwner(id, isLazy)).thenReturn(Optional.of(owner));
        when(repository.deleteOwner(id)).thenReturn(0);

        ownerService.deleteOwnerById(id, response);

        verify(repository).getOwner(id, isLazy);
        verify(repository).deleteOwner(id);
        verify(response).setStatus(400);
    }

    @Test
    void deleteOwnerById_WhenOwnerNotFound_ThenNotFoundException() {
        var id = 15L;
        var isLazy = true;
        when(repository.getOwner(id, isLazy)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> ownerService.deleteOwnerById(id, response)
                , String.format("Пользователь с id=%d не найден", id));
    }
}