package ru.aston.hw3.owner.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aston.hw3.exception.NotFoundException;
import ru.aston.hw3.owner.OwnerRepository;
import ru.aston.hw3.owner.mapper.OwnerMapper;
import ru.aston.hw3.owner.model.Owner;
import ru.aston.hw3.owner.model.dto.InputOwnerDto;
import ru.aston.hw3.repository.JDBCPostgres;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {
    private final OwnerRepository repository;
    private final OwnerMapper ownerMapper;
    private final Gson gson = new Gson();

    public OwnerServiceImpl() {
        repository = new JDBCPostgres();
        ownerMapper = OwnerMapper.INSTANCE;
    }

    @Override
    public HttpServletResponse addOwner(InputOwnerDto inputOwnerDto, HttpServletResponse resp) {
        Owner owner = ownerMapper.toOwner(inputOwnerDto);
        Optional<Owner> ownerOptional = repository.addOwner(owner);
        log.info("OS: addOwner={}", ownerOptional);
        return getHttpServletResponse(resp, ownerOptional);
    }

    @Override
    public HttpServletResponse getOwnerById(long id, boolean isLazy, HttpServletResponse resp) {
        Owner owner = findOwnerById(id, isLazy);
        log.info("OS: getOwnerById={}, returned owner={}", id, owner);
        return getHttpServletResponse(resp, owner);
    }

    @Override
    public HttpServletResponse deleteOwnerById(long id, HttpServletResponse resp) {
        findOwnerById(id, true);
        int numOfDeletedOwners = repository.deleteOwner(id);
        log.info("OS: deleteOwnerById={}, num of deleted={}", id, numOfDeletedOwners);
        if (numOfDeletedOwners > 0) {
            resp.setStatus(204);
        } else {
            log.error("ни одна запись не была удалена");
            resp.setStatus(400);
        }
        return resp;
    }

    private HttpServletResponse getHttpServletResponse(HttpServletResponse resp, Owner newOwner) {
        try {
            resp.getWriter().write(this.gson.toJson(ownerMapper.toOutOwnerDto(newOwner)));
        } catch (RuntimeException | IOException e) {
            log.error("RuntimeException | IOException");
            e.printStackTrace();
            resp.setStatus(500);
        }
        return resp;
    }

    private HttpServletResponse getHttpServletResponse(HttpServletResponse resp, Optional<Owner> owner) {
        owner.ifPresentOrElse(value -> getHttpServletResponse(resp, value), () -> {
            log.error("нет объекта для передачи");
            resp.setStatus(400);
        });
        return resp;
    }

    private Owner findOwnerById(long id, boolean isLazy) {
        return repository.getOwner(id, isLazy).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

}
