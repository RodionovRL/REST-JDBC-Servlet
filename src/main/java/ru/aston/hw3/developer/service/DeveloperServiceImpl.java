package ru.aston.hw3.developer.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aston.hw3.exception.NotFoundException;
import ru.aston.hw3.developer.DeveloperRepository;
import ru.aston.hw3.developer.mapper.DeveloperMapper;
import ru.aston.hw3.developer.model.Developer;
import ru.aston.hw3.developer.model.dto.InputDeveloperDto;
import ru.aston.hw3.repository.JDBCPostgres;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DeveloperServiceImpl implements DeveloperService {
    private final DeveloperRepository repository;
    private final DeveloperMapper developerMapper;
    private final Gson gson = new Gson();

    public DeveloperServiceImpl() {
        repository = new JDBCPostgres();
        developerMapper = DeveloperMapper.INSTANCE;
    }

    @Override
    public HttpServletResponse addDeveloper(InputDeveloperDto inputDeveloperDto, HttpServletResponse resp) {
        Developer developer = developerMapper.toDeveloper(inputDeveloperDto);
        Optional<Developer> developerOptional = repository.addDeveloper(developer);
        log.info("OS: addDeveloper={}", developerOptional);
        return getHttpServletResponse(resp, developerOptional);
    }

    @Override
    public HttpServletResponse getDeveloperById(long id, boolean isLazy, HttpServletResponse resp) {
        Developer developer = findDeveloperById(id, isLazy);
        log.info("OS: getDeveloperById={}, returned developer={}", id, developer);
        return getHttpServletResponse(resp, developer);
    }

    @Override
    public HttpServletResponse deleteDeveloperById(long id, HttpServletResponse resp) {
        findDeveloperById(id, true);
        int numOfDeletedDevelopers = repository.deleteDeveloper(id);
        log.info("DS: deleteDeveloperById={}, num of deleted={}", id, numOfDeletedDevelopers);
        if (numOfDeletedDevelopers > 0) {
            resp.setStatus(204);
        } else {
            log.error("ни одна запись не была удалена");
            resp.setStatus(400);
        }
        return resp;
    }

    private HttpServletResponse getHttpServletResponse(HttpServletResponse resp, Developer newDeveloper) {
        try {
            resp.getWriter().write(this.gson.toJson(developerMapper.toOutDeveloperDto(newDeveloper)));
        } catch (JsonSyntaxException | IOException e) {
            log.error("JsonSyntaxException | IOException");
            e.printStackTrace();
            resp.setStatus(500);
        }
        return resp;
    }

    private HttpServletResponse getHttpServletResponse(HttpServletResponse resp, Optional<Developer> developer) {
        developer.ifPresentOrElse(value -> getHttpServletResponse(resp, value), () -> {
            log.error("нет объекта для передачи");
            resp.setStatus(400);
        });
        return resp;
    }

    private Developer findDeveloperById(long id, boolean isLazy) {
        return repository.getDeveloper(id, isLazy).orElseThrow(
                () -> new NotFoundException(String.format("Разработчик с id=%d не найден", id)));
    }
}
