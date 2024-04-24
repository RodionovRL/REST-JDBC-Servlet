package ru.aston.hw3.developer.service;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hw3.developer.DeveloperRepository;
import ru.aston.hw3.developer.mapper.DeveloperMapper;
import ru.aston.hw3.developer.model.Developer;
import ru.aston.hw3.developer.model.dto.InputDeveloperDto;
import ru.aston.hw3.exception.NotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceImplTest {
    @Mock
    private DeveloperRepository repository;
    @Mock
    private DeveloperMapper developerMapper;
    @InjectMocks
    private DeveloperServiceImpl developerService;
    HttpServletResponse response = mock(HttpServletResponse.class);
    private final Gson gson = new Gson();
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);

    @Test
    @SneakyThrows
    void addDeveloper_WhenAllDataIsGood() {

        InputDeveloperDto inputDeveloperDto = InputDeveloperDto.builder()
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        Developer developer = Developer.builder()
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        when(developerMapper.toDeveloper(inputDeveloperDto)).thenReturn(developer);
        when(repository.addDeveloper(developer)).thenReturn(Optional.of(developer));
        when(response.getWriter()).thenReturn(writer);

        developerService.addDeveloper(inputDeveloperDto, response);

        verify(developerMapper).toDeveloper(inputDeveloperDto);
        verify(repository).addDeveloper(developer);
        verify(response).getWriter();
        verify(developerMapper).toOutDeveloperDto(any(Developer.class));
        assertTrue(stringWriter.toString().contains(gson.toJson(developerMapper.toOutDeveloperDto(developer))));
    }

    @Test
    @SneakyThrows
    void getDeveloperById_WhenAllDataIsGood() {
        var id = 15L;
        var isLazy = true;
        Developer developer = Developer.builder()
                .id(id)
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        when(repository.getDeveloper(id, isLazy)).thenReturn(Optional.of(developer));
        when(response.getWriter()).thenReturn(writer);

        developerService.getDeveloperById(id, isLazy, response);

        verify(repository).getDeveloper(id, isLazy);
        verify(response).getWriter();
        verify(developerMapper).toOutDeveloperDto(any(Developer.class));
        assertTrue(stringWriter.toString().contains(gson.toJson(developerMapper.toOutDeveloperDto(developer))));
    }

    @Test
    void getDeveloperById_WhenCameEmptyFromRepository_WhenNotFoundException() {
        var id = 15L;
        var isLazy = true;
        when(repository.getDeveloper(id, isLazy)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> developerService.getDeveloperById(id, isLazy, response)
                , String.format("Разработчик с id=%d не найден", id));
    }

    @Test
    void deleteDeveloperById_WhenDeveloperFoundAndDeleted_ThenReturnStatus204() {
        var id = 15L;
        var isLazy = true;
        Developer developer = Developer.builder()
                .id(id)
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        when(repository.getDeveloper(id, isLazy)).thenReturn(Optional.of(developer));
        when(repository.deleteDeveloper(id)).thenReturn(1);

        developerService.deleteDeveloperById(id, response);

        verify(repository).getDeveloper(id, isLazy);
        verify(repository).deleteDeveloper(id);
        verify(response).setStatus(204);
    }

    @Test
    void deleteDeveloperById_WhenDeveloperFoundButNotDeleted_ThenReturnStatus400() {
        var id = 15L;
        var isLazy = true;
        Developer developer = Developer.builder()
                .id(id)
                .lastName("lastName")
                .firstName("firstName")
                .email("email")
                .build();
        when(repository.getDeveloper(id, isLazy)).thenReturn(Optional.of(developer));
        when(repository.deleteDeveloper(id)).thenReturn(0);

        developerService.deleteDeveloperById(id, response);

        verify(repository).getDeveloper(id, isLazy);
        verify(repository).deleteDeveloper(id);
        verify(response).setStatus(400);
    }

    @Test
    void deleteDeveloperById_WhenDeveloperNotFound_ThenNotFoundException() {
        var id = 15L;
        var isLazy = true;
        when(repository.getDeveloper(id, isLazy)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> developerService.deleteDeveloperById(id, response)
                , String.format("Разработчик с id=%d не найден", id));
    }
}