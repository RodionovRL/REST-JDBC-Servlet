package ru.aston.hw3.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.delegate.DatabaseDelegate;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.hw3.developer.model.Developer;
import ru.aston.hw3.owner.model.Owner;
import ru.aston.hw3.solution.model.Solution;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JDBCPostgresTest {
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:14-alpine");

    DatabaseDelegate delegate = new JdbcDatabaseDelegate(postgres, "");

    JDBCPostgres repository;

    Owner owner = Owner.builder()
            .lastName("lastName")
            .firstName("firstName")
            .email("emailTest")
            .build();
    Developer developer = Developer.builder()
            .lastName("lastNameDeveloper")
            .firstName("firstNameDeveloper")
            .email("emailTestDeveloper")
            .build();
    Developer secondDeveloper = Developer.builder()
            .lastName("lastNameSecondDeveloper")
            .firstName("firstNameSecondDeveloper")
            .email("emailTestSecondDeveloper")
            .build();
    Solution solution = Solution.builder()
            .name("name")
            .version("version")
            .build();
    Solution secondSolution = Solution.builder()
            .name("secondSolution")
            .version("secondVersion")
            .build();


    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(delegate, "schema.sql");
        repository = new JDBCPostgres(
                postgres.getDriverClassName(),
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @Test
    @Order(1)
    void addOwner() {

        Optional<Owner> addedOwner = repository.addOwner(owner);

        assertTrue(addedOwner.isPresent());
        assertEquals(1L, addedOwner.get().getId());
        assertEquals(owner.getLastName(), addedOwner.get().getLastName());
        assertEquals(owner.getFirstName(), addedOwner.get().getFirstName());
        assertEquals(owner.getEmail(), addedOwner.get().getEmail());
    }

    @Test
    @Order(2)
    void getOwner_WhenIsLazy_ThenReturnOwnerWithoutSolutions() {
        var id = 1L;
        var isLazy = true;

        Optional<Owner> resultOwner = repository.getOwner(id, isLazy);

        assertTrue(resultOwner.isPresent());
        assertEquals(owner.getLastName(), resultOwner.get().getLastName());
        assertEquals(owner.getFirstName(), resultOwner.get().getFirstName());
        assertEquals(owner.getEmail(), resultOwner.get().getEmail());
        assertNull(resultOwner.get().getSolutions());
    }

    @Test
    @Order(3)
    void addSolutionWithoutOwner() {

        Optional<Solution> addedSolution = repository.addSolution(solution);

        assertTrue(addedSolution.isPresent());
        assertEquals(1L, addedSolution.get().getId());
        assertEquals(solution.getName(), addedSolution.get().getName());
        assertEquals(solution.getVersion(), addedSolution.get().getVersion());
        assertEquals(solution.getOwner(), addedSolution.get().getOwner());

    }

    @Test
    @Order(4)
    void addSolutionWithOwner() {
        owner.setId(1L);
        secondSolution.setOwner(owner);

        Optional<Solution> addedSolution = repository.addSolution(secondSolution);

        assertTrue(addedSolution.isPresent());
        assertEquals(2L, addedSolution.get().getId());
        assertEquals(secondSolution.getName(), addedSolution.get().getName());
        assertEquals(secondSolution.getVersion(), addedSolution.get().getVersion());
        assertEquals(secondSolution.getOwner(), addedSolution.get().getOwner());
    }

    @Test
    @Order(5)
    void getSolution_WhenIsLazy_ThenReturnSolutionWithoutOwnerAndDevelopers() {
        var id = 2L;
        var isLazy = true;

        Optional<Solution> resultSolution = repository.getSolution(id, isLazy);

        assertTrue(resultSolution.isPresent());
        assertEquals(secondSolution.getName(), resultSolution.get().getName());
        assertEquals(secondSolution.getVersion(), resultSolution.get().getVersion());
        assertEquals(secondSolution.getOwner(), resultSolution.get().getOwner());
        assertNull(resultSolution.get().getOwner());
        assertNull(resultSolution.get().getDevelopers());
    }

    @Test
    @Order(6)
    void addDeveloper() {
        Optional<Developer> addedDeveloper = repository.addDeveloper(developer);

        assertTrue(addedDeveloper.isPresent());
        assertEquals(1L, addedDeveloper.get().getId());
        assertEquals(developer.getLastName(), addedDeveloper.get().getLastName());
        assertEquals(developer.getFirstName(), addedDeveloper.get().getFirstName());
        assertEquals(developer.getEmail(), addedDeveloper.get().getEmail());
    }

    @Test
    @Order(7)
    void addSecondDeveloper() {
        Optional<Developer> addedDeveloper = repository.addDeveloper(secondDeveloper);

        assertTrue(addedDeveloper.isPresent());
        assertEquals(2L, addedDeveloper.get().getId());
        assertEquals(secondDeveloper.getLastName(), addedDeveloper.get().getLastName());
        assertEquals(secondDeveloper.getFirstName(), addedDeveloper.get().getFirstName());
        assertEquals(secondDeveloper.getEmail(), addedDeveloper.get().getEmail());
    }

    @Test
    @Order(8)
    void getDeveloper_WhenIsLazy_ThenReturnDeveloperWithoutSolutions() {
        var id = 1L;
        var isLazy = true;

        Optional<Developer> resultDeveloper = repository.getDeveloper(id, isLazy);

        assertTrue(resultDeveloper.isPresent());
        assertEquals(developer.getLastName(), resultDeveloper.get().getLastName());
        assertEquals(developer.getFirstName(), resultDeveloper.get().getFirstName());
        assertEquals(developer.getEmail(), resultDeveloper.get().getEmail());
        assertNull(resultDeveloper.get().getSolutions());
    }

    @Test
    @Order(9)
    void setSolutionDeveloper_WhenAllDataIsGood_ThenReturnTrue() {
        var solutionId = 1L;
        var developerId = 1L;

        boolean result = repository.setSolutionDeveloper(solutionId, developerId);

        assertTrue(result);
    }

    @Test
    @Order(10)
    void setSolutionDeveloper_WhenSolutionIdIsNull_ThenReturnFalse() {
        Long solutionId = null;
        var developerId = 1L;

        boolean result = repository.setSolutionDeveloper(solutionId, developerId);

        assertFalse(result);
    }

    @Test
    @Order(11)
    void setSolutionDeveloper_WhenDeveloperIdIsNull_ThenReturnFalse() {
        var solutionId = 1L;
        Long developerId = null;

        boolean result = repository.setSolutionDeveloper(solutionId, developerId);

        assertFalse(result);
    }

    @Test
    @Order(12)
    void setSolutionOwner_WhenAllDataIsGood_ThenReturnTrue() {
        var solutionId = 1L;
        var ownerId = 1L;

        boolean result = repository.setSolutionOwner(solutionId, ownerId);

        assertTrue(result);
    }

    @Test
    @Order(13)
    void setSolutionOwner_WhenSolutionIdIsNull_ThenReturnFalse() {
        Long solutionId = null;
        var ownerId = 1L;

        boolean result = repository.setSolutionOwner(solutionId, ownerId);

        assertFalse(result);
    }

    @Test
    @Order(14)
    void setSolutionOwner_WhenOwnerIdIsNull_ThenReturnFalse() {
        var solutionId = 1L;
        Long ownerId = null;

        boolean result = repository.setSolutionOwner(solutionId, ownerId);

        assertFalse(result);
    }

    @Test
    @Order(15)
    void getSolution_WhenIsLazyFalse_ThenSolutionReturnWithOwnerAndDevelopers() {
        var solutionId = 1L;
        var developerId = 2L;
        var isLazy = false;
        repository.setSolutionDeveloper(solutionId, developerId);

        Optional<Solution> resultSolution = repository.getSolution(solutionId, isLazy);

        assertTrue(resultSolution.isPresent());
        assertEquals(solution.getName(), resultSolution.get().getName());
        assertEquals(solution.getVersion(), resultSolution.get().getVersion());
        assertNotNull(resultSolution.get().getOwner());
        assertNotNull(resultSolution.get().getDevelopers());
        assertEquals(1L, resultSolution.get().getOwner().getId());
        assertEquals(2, resultSolution.get().getDevelopers().size());
    }

    @Test
    @Order(16)
    void getOwner_WhenIsLazyFalse_ThenOwnerReturnWithSolutions() {
        var solutionId2 = 2L;
        var ownerId = 1L;
        var isLazy = false;
        repository.setSolutionOwner(solutionId2, ownerId);

        Optional<Owner> resultOwner = repository.getOwner(ownerId, isLazy);

        assertTrue(resultOwner.isPresent());
        assertEquals(owner.getFirstName(), resultOwner.get().getFirstName());
        assertEquals(owner.getLastName(), resultOwner.get().getLastName());
        assertEquals(owner.getEmail(), resultOwner.get().getEmail());
        assertNotNull(resultOwner.get().getSolutions());
        assertEquals(2, resultOwner.get().getSolutions().size());
    }

    @Test
    @Order(17)
    void getDeveloper_WhenIsLazyFalse_TheDeveloperReturnWithSolutions() {
        var solutionId2 = 2L;
        var developerId = 1L;
        var isLazy = false;
        repository.setSolutionDeveloper(solutionId2, developerId);

        Optional<Developer> resultDeveloper = repository.getDeveloper(developerId, isLazy);

        assertTrue(resultDeveloper.isPresent());
        assertEquals(developer.getFirstName(), resultDeveloper.get().getFirstName());
        assertEquals(developer.getLastName(), resultDeveloper.get().getLastName());
        assertEquals(developer.getEmail(), resultDeveloper.get().getEmail());
        assertNotNull(resultDeveloper.get().getSolutions());
        assertEquals(2, resultDeveloper.get().getSolutions().size());
    }

    @Test
    @Order(18)
    void deleteOwner() {
        var id = 1L;

        int numDeletedOwners = repository.deleteOwner(id);
        Optional<Owner> resultOwner = repository.getOwner(id, true);

        assertEquals(1, numDeletedOwners);
        assertTrue(resultOwner.isEmpty());
    }

    @Test
    @Order(19)
    void deleteSolution() {
        var id = 2L;

        int numDeletedSolutions = repository.deleteSolution(id);
        Optional<Solution> resultSolution = repository.getSolution(id, true);

        assertEquals(1, numDeletedSolutions);
        assertTrue(resultSolution.isEmpty());
    }

    @Test
    @Order(20)
    void deleteDeveloper() {
        var id = 1L;

        int numDeletedDevelopers = repository.deleteDeveloper(id);
        Optional<Developer> resultDeveloper = repository.getDeveloper(id, true);

        assertEquals(1, numDeletedDevelopers);
        assertTrue(resultDeveloper.isEmpty());
    }
}