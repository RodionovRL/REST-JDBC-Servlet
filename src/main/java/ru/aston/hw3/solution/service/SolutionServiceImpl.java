package ru.aston.hw3.solution.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aston.hw3.developer.DeveloperRepository;
import ru.aston.hw3.developer.model.Developer;
import ru.aston.hw3.exception.NotFoundException;
import ru.aston.hw3.owner.OwnerRepository;
import ru.aston.hw3.owner.model.Owner;
import ru.aston.hw3.repository.JDBCPostgres;
import ru.aston.hw3.solution.SolutionRepository;
import ru.aston.hw3.solution.mapper.SolutionMapper;
import ru.aston.hw3.solution.model.Solution;
import ru.aston.hw3.solution.model.dto.InputSolutionDto;
import ru.aston.hw3.solution.model.dto.InputSolutionLinkDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SolutionServiceImpl implements SolutionService {
    private final SolutionRepository repository;
    private final OwnerRepository ownerRepository;
    private final DeveloperRepository developerRepository;
    private final SolutionMapper solutionMapper;
    private final Gson gson = new Gson();

    public SolutionServiceImpl() {
        repository = new JDBCPostgres();
        ownerRepository = new JDBCPostgres();
        developerRepository = new JDBCPostgres();
        solutionMapper = SolutionMapper.INSTANCE;
    }

    @Override
    public HttpServletResponse addSolution(InputSolutionDto inputSolutionDto, HttpServletResponse resp) {
        Solution solution = solutionMapper.toSolution(inputSolutionDto);
        if (inputSolutionDto.getOwnerId() != null) {
            Owner owner = findOwnerById(inputSolutionDto.getOwnerId(), true);
            solution.setOwner(owner);
        }
        Optional<Solution> solutionOptional = repository.addSolution(solution);
        log.info("SolS: addSolution={}", solutionOptional);
        return getHttpServletResponse(resp, solutionOptional);
    }

    @Override
    public HttpServletResponse getSolutionById(long id, boolean isLazy, HttpServletResponse resp) {
        Solution solution = findSolutionById(id, isLazy);
        log.info("SolS: getSolutionById={}, returned solution={}", id, solution);
        return getHttpServletResponse(resp, solution);
    }

    @Override
    public HttpServletResponse deleteSolutionById(long id, HttpServletResponse resp) {
        findSolutionById(id, true);
        int numOfDeletedSolutions = repository.deleteSolution(id);
        log.info("SolS: deleteSolutionById={}, num of deleted={}", id, numOfDeletedSolutions);
        if (numOfDeletedSolutions > 0) {
            resp.setStatus(204);
        } else {
            log.error("ни одна запись не была удалена");
            resp.setStatus(400);
        }
        return resp;
    }

    @Override
    public boolean addSolutionLink(InputSolutionLinkDto newSolutionLink) {
        boolean setSolutionDeveloperResult;
        boolean setSolutionOwnerResult;
        Long solutionId = newSolutionLink.getSolutionId();
        Long developerId = newSolutionLink.getDeveloperId();
        Long ownerId = newSolutionLink.getOwnerId();
        if (solutionId == null) {
            log.error("SSL: solutionId = null");
            return false;
        }
        if (developerId == null && ownerId == null) {
            log.error("SSL: developerId={}, ownerId={}", developerId, ownerId);
            return false;
        }
        findSolutionById(solutionId, true);
        if (developerId != null) {
            log.info("SSL: add link solutionId={}, developerId={}", solutionId, developerId);
            findDeveloperById(developerId, true);
            setSolutionDeveloperResult = repository.setSolutionDeveloper(solutionId, developerId);
        } else {
            log.info("SSL: developerId={}", developerId);
            setSolutionDeveloperResult = true;
        }
        if (ownerId != null) {
            log.info("SSL: add link solutionId={}, ownerId={}", solutionId, ownerId);
            findOwnerById(ownerId, true);
            setSolutionOwnerResult = repository.setSolutionOwner(solutionId, ownerId);
        } else {
            log.info("SSL: ownerId={}", ownerId);
            setSolutionOwnerResult = true;
        }
        return setSolutionDeveloperResult && setSolutionOwnerResult;
    }

    private HttpServletResponse getHttpServletResponse(HttpServletResponse resp, Solution solution) {
        try {
            resp.getWriter().write(this.gson.toJson(solutionMapper.toOutSolutionDto(solution)));
        } catch (JsonSyntaxException | IOException e) {
            log.error("JsonSyntaxException | IOException");
            e.printStackTrace();
            resp.setStatus(500);
        }
        return resp;
    }

    private HttpServletResponse getHttpServletResponse(HttpServletResponse resp, Optional<Solution> solution) {
        solution.ifPresentOrElse(value -> getHttpServletResponse(resp, value), () -> {
            log.error("нет объекта для передачи");
            resp.setStatus(400);
        });
        return resp;
    }

    private Solution findSolutionById(long id, boolean isLazy) {
        return repository.getSolution(id, isLazy).orElseThrow(
                () -> new NotFoundException(String.format("Проект с id=%d не найден", id)));
    }

    private Owner findOwnerById(long id, boolean isLazy) {
        return ownerRepository.getOwner(id, isLazy).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    private Developer findDeveloperById(long id, boolean isLazy) {
        return developerRepository.getDeveloper(id, isLazy).orElseThrow(
                () -> new NotFoundException(String.format("Разработчик с id=%d не найден", id)));
    }
}

