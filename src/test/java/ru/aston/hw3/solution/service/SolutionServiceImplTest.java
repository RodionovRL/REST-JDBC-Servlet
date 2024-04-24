package ru.aston.hw3.solution.service;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hw3.developer.DeveloperRepository;
import ru.aston.hw3.developer.model.Developer;
import ru.aston.hw3.exception.NotFoundException;
import ru.aston.hw3.owner.OwnerRepository;
import ru.aston.hw3.owner.model.Owner;
import ru.aston.hw3.solution.SolutionRepository;
import ru.aston.hw3.solution.mapper.SolutionMapper;
import ru.aston.hw3.solution.model.Solution;
import ru.aston.hw3.solution.model.dto.InputSolutionDto;
import ru.aston.hw3.solution.model.dto.InputSolutionLinkDto;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionServiceImplTest {
    @Mock
    private SolutionRepository repository;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private DeveloperRepository developerRepository;
    @Mock
    private SolutionMapper solutionMapper;
    @InjectMocks
    private SolutionServiceImpl solutionService;
    HttpServletResponse response = mock(HttpServletResponse.class);
    private final Gson gson = new Gson();
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);

    @Test
    @SneakyThrows
    void addSolution_WhenAllDataIsGood() {
        var ownerId = 90L;
        Owner owner = Owner.builder()
                .id(ownerId)
                .lastName("lastNameOwner")
                .firstName("firstNameOwner")
                .email("emailOwner")
                .build();
        InputSolutionDto inputSolutionDto = InputSolutionDto.builder()
                .name("nameSolution")
                .version("versionSolution")
                .ownerId(ownerId)
                .build();
        Solution solution = Solution.builder()
                .name("name")
                .version("version")
                .owner(null)
                .build();
        solution.setOwner(owner);
        when(solutionMapper.toSolution(inputSolutionDto)).thenReturn(solution);
        when(ownerRepository.getOwner(ownerId, true)).thenReturn(Optional.of(owner));
        when(repository.addSolution(solution)).thenReturn(Optional.of(solution));
        when(response.getWriter()).thenReturn(writer);

        solutionService.addSolution(inputSolutionDto, response);

        verify(solutionMapper).toSolution(inputSolutionDto);
        verify(repository).addSolution(solution);
        verify(response).getWriter();
        verify(solutionMapper).toOutSolutionDto(any(Solution.class));
        assertTrue(stringWriter.toString().contains(gson.toJson(solutionMapper.toOutSolutionDto(solution))));
    }

    @Test
    @SneakyThrows
    void getSolutionById_WhenAllDataIsGoodAndLazyIsTrue_ThenReturnSolutionWithOwnerAndDevelopersIsNull() {
        var id = 15L;
        var isLazy = true;
        Solution solution = Solution.builder()
                .name("name")
                .version("version")
                .owner(null)
                .build();
        when(repository.getSolution(id, isLazy)).thenReturn(Optional.of(solution));
        when(response.getWriter()).thenReturn(writer);

        solutionService.getSolutionById(id, isLazy, response);

        verify(repository).getSolution(id, isLazy);
        verify(response).getWriter();
        verify(solutionMapper).toOutSolutionDto(any(Solution.class));
        verify(ownerRepository, never()).getOwner(anyLong(), anyBoolean());
        assertTrue(stringWriter.toString().contains(gson.toJson(solutionMapper.toOutSolutionDto(solution))));
        assertNull(solution.getOwner());
        assertNull(solution.getDevelopers());
    }

    @Test
    @SneakyThrows
    void getSolutionById_WhenAllDataIsGoodAndLazyIsTrue_ThenReturnSolutionWithOwnerAndDevelopersIsNotNull() {
        var id = 15L;
        var isLazy = false;
        var ownerId = 23L;
        var developerId1 = 34L;
        var developerId2 = 35L;
        var developerId3 = 36L;
        Owner owner = Owner.builder()
                .id(ownerId)
                .lastName("lastNameOwner")
                .firstName("firstNameOwner")
                .email("emailOwner")
                .build();
        Developer developer1 = Developer.builder()
                .id(developerId1)
                .lastName("lastNameDeveloper1")
                .firstName("firstNameDeveloper1")
                .email("emailDeveloper1")
                .build();
        Developer developer2 = Developer.builder()
                .id(developerId2)
                .lastName("lastNameDeveloper2")
                .firstName("firstNameDeveloper2")
                .email("emailDeveloper2")
                .build();
        Developer developer3 = Developer.builder()
                .id(developerId3)
                .lastName("lastNameDeveloper3")
                .firstName("firstNameDeveloper3")
                .email("emailDeveloper3")
                .build();
        List<Developer> developerList = new ArrayList<>(List.of(developer1, developer2, developer3));
        Solution solution = Solution.builder()
                .name("name")
                .version("version")
                .owner(owner)
                .developers(developerList)
                .build();
        when(repository.getSolution(id, isLazy)).thenReturn(Optional.of(solution));
        when(response.getWriter()).thenReturn(writer);

        solutionService.getSolutionById(id, isLazy, response);

        verify(repository).getSolution(id, isLazy);
        verify(response).getWriter();
        verify(solutionMapper).toOutSolutionDto(any(Solution.class));
        verify(ownerRepository, never()).getOwner(anyLong(), anyBoolean());
        assertTrue(stringWriter.toString().contains(gson.toJson(solutionMapper.toOutSolutionDto(solution))));
        assertNotNull(solution.getOwner());
        assertNotNull(solution.getDevelopers());
    }

    @Test
    void deleteSolutionById_WhenSolutionFoundAndDeleted_ThenReturnStatus204() {
        var id = 15L;
        var isLazy = true;
        Solution solution = Solution.builder()
                .name("name")
                .version("version")
                .owner(null)
                .build();
        when(repository.getSolution(id, isLazy)).thenReturn(Optional.of(solution));
        when(repository.deleteSolution(id)).thenReturn(1);

        solutionService.deleteSolutionById(id, response);

        verify(repository).getSolution(id, isLazy);
        verify(repository).deleteSolution(id);
        verify(response).setStatus(204);
    }

    @Test
    void deleteSolutionById_WhenSolutionFoundButNotDeleted_ThenReturnStatus400() {
        var id = 15L;
        var isLazy = true;
        Solution solution = Solution.builder()
                .name("name")
                .version("version")
                .owner(null)
                .build();
        when(repository.getSolution(id, isLazy)).thenReturn(Optional.of(solution));
        when(repository.deleteSolution(id)).thenReturn(0);

        solutionService.deleteSolutionById(id, response);

        verify(repository).getSolution(id, isLazy);
        verify(repository).deleteSolution(id);
        verify(response).setStatus(400);
    }

    @Test
    void deleteSolutionById_WhenSolutionNotFound_ThenNotFoundException() {
        var id = 15L;
        var isLazy = true;
        when(repository.getSolution(id, isLazy)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> solutionService.deleteSolutionById(id, response)
                , String.format("Проект с id=%d не найден", id));
    }

    @Test
    void addSolutionLink_whenAllDataIsGoodAndNotNull_ThenResultIsTrue() {
        var solutionId = 15L;
        var isLazy = true;
        var ownerId = 23L;
        var developerId = 34L;
        InputSolutionLinkDto inputSolutionLinkDto = InputSolutionLinkDto.builder()
                .solutionId(solutionId)
                .ownerId(ownerId)
                .developerId(developerId)
                .build();
        Owner owner = Owner.builder()
                .id(ownerId)
                .lastName("lastNameOwner")
                .firstName("firstNameOwner")
                .email("emailOwner")
                .build();
        Developer developer = Developer.builder()
                .id(developerId)
                .lastName("lastNameDeveloper1")
                .firstName("firstNameDeveloper1")
                .email("emailDeveloper1")
                .build();
        Solution solution = Solution.builder()
                .name("name")
                .version("version")
                .build();
        when(repository.getSolution(solutionId, isLazy)).thenReturn(Optional.of(solution));
        when(developerRepository.getDeveloper(developerId, isLazy)).thenReturn(Optional.of(developer));
        when(repository.setSolutionDeveloper(solutionId, developerId)).thenReturn(true);
        when(ownerRepository.getOwner(ownerId, isLazy)).thenReturn(Optional.of(owner));
        when(repository.setSolutionOwner(solutionId, ownerId)).thenReturn(true);

        boolean result = solutionService.addSolutionLink(inputSolutionLinkDto);

        verify(repository).getSolution(solutionId, isLazy);
        verify(developerRepository).getDeveloper(developerId, isLazy);
        verify(repository).setSolutionDeveloper(solutionId, developerId);
        verify(ownerRepository).getOwner(ownerId, isLazy);
        verify(repository).setSolutionOwner(solutionId, ownerId);
        assertTrue(result);
    }

    @Test
    void addSolutionLink_whenAllDataIsGoodAndOwnerIsNull_ThenResultIsTrueAndNeverUseOwnerRepository() {
        var solutionId = 15L;
        var isLazy = true;
        Long ownerId = null;
        var developerId = 34L;
        InputSolutionLinkDto inputSolutionLinkDto = InputSolutionLinkDto.builder()
                .solutionId(solutionId)
                .ownerId(ownerId)
                .developerId(developerId)
                .build();
        Developer developer = Developer.builder()
                .id(developerId)
                .lastName("lastNameDeveloper1")
                .firstName("firstNameDeveloper1")
                .email("emailDeveloper1")
                .build();
        Solution solution = Solution.builder()
                .name("name")
                .version("version")
                .build();
        when(repository.getSolution(solutionId, isLazy)).thenReturn(Optional.of(solution));
        when(developerRepository.getDeveloper(developerId, isLazy)).thenReturn(Optional.of(developer));
        when(repository.setSolutionDeveloper(solutionId, developerId)).thenReturn(true);

        boolean result = solutionService.addSolutionLink(inputSolutionLinkDto);

        verify(repository).getSolution(solutionId, isLazy);
        verify(developerRepository).getDeveloper(developerId, isLazy);
        verify(repository).setSolutionDeveloper(solutionId, developerId);
        verify(ownerRepository, never()).getOwner(anyLong(), anyBoolean());
        verify(repository, never()).setSolutionOwner(anyLong(), anyLong());
        assertTrue(result);
    }

    @Test
    void addSolutionLink_whenAllDataIsGoodAndDeveloperIsNull_ThenResultIsTrueAndNeverUseDeveloperRepository() {
        var solutionId = 15L;
        var isLazy = true;
        var ownerId = 23L;
        Long developerId = null;
        InputSolutionLinkDto inputSolutionLinkDto = InputSolutionLinkDto.builder()
                .solutionId(solutionId)
                .ownerId(ownerId)
                .developerId(developerId)
                .build();
        Owner owner = Owner.builder()
                .id(ownerId)
                .lastName("lastNameOwner")
                .firstName("firstNameOwner")
                .email("emailOwner")
                .build();
        Solution solution = Solution.builder()
                .name("name")
                .version("version")
                .build();
        when(repository.getSolution(solutionId, isLazy)).thenReturn(Optional.of(solution));
        when(ownerRepository.getOwner(ownerId, isLazy)).thenReturn(Optional.of(owner));
        when(repository.setSolutionOwner(solutionId, ownerId)).thenReturn(true);

        boolean result = solutionService.addSolutionLink(inputSolutionLinkDto);

        verify(repository).getSolution(solutionId, isLazy);
        verify(developerRepository, never()).getDeveloper(anyLong(), anyBoolean());
        verify(repository, never()).setSolutionDeveloper(anyLong(), anyLong());
        verify(ownerRepository).getOwner(ownerId, isLazy);
        verify(repository).setSolutionOwner(solutionId, ownerId);
        assertTrue(result);
    }

    @Test
    void addSolutionLink_whenOwnerAndDeveloperIsNull_ThenReturnFalse() {
        var solutionId = 15L;
        Long ownerId = null;
        Long developerId = null;
        InputSolutionLinkDto inputSolutionLinkDto = InputSolutionLinkDto.builder()
                .solutionId(solutionId)
                .ownerId(ownerId)
                .developerId(developerId)
                .build();

        boolean result = solutionService.addSolutionLink(inputSolutionLinkDto);

        verify(repository, never()).getSolution(anyLong(), anyBoolean());
        verify(developerRepository, never()).getDeveloper(anyLong(), anyBoolean());
        verify(repository, never()).setSolutionDeveloper(anyLong(), anyLong());
        verify(ownerRepository, never()).getOwner(anyLong(), anyBoolean());
        verify(repository, never()).setSolutionOwner(anyLong(), anyLong());
        assertFalse(result);
    }

    @Test
    void addSolutionLink_whenSolutionIdIsNull() {
        Long solutionId = null;
        Long ownerId = null;
        Long developerId = null;
        InputSolutionLinkDto inputSolutionLinkDto = InputSolutionLinkDto.builder()
                .solutionId(solutionId)
                .ownerId(ownerId)
                .developerId(developerId)
                .build();

        boolean result = solutionService.addSolutionLink(inputSolutionLinkDto);

        verify(repository, never()).getSolution(anyLong(), anyBoolean());
        verify(developerRepository, never()).getDeveloper(anyLong(), anyBoolean());
        verify(repository, never()).setSolutionDeveloper(anyLong(), anyLong());
        verify(ownerRepository, never()).getOwner(anyLong(), anyBoolean());
        verify(repository, never()).setSolutionOwner(anyLong(), anyLong());
        assertFalse(result);
    }
}