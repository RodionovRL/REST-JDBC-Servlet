package ru.aston.hw3.owner.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.aston.hw3.owner.model.dto.InputOwnerDto;
import ru.aston.hw3.owner.service.OwnerService;
import ru.aston.hw3.owner.service.OwnerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
@WebServlet("/owners")
public class OwnerController extends HttpServlet {
    private OwnerService ownerService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ownerService = new OwnerServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        long id;
        boolean isLazy = true;
        try {
            id = Long.parseLong(req.getParameter("id"));
            if (req.getParameter("short") != null) {
                isLazy = Boolean.parseBoolean(req.getParameter("short"));
            }

            log.info("get Owner by id={} isLazy={}", id, isLazy);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            ownerService.getOwnerById(id, isLazy, resp);
        } catch (NumberFormatException e) {
            log.error("getOwner Exception");
            e.printStackTrace();
            try {
                resp.sendError(500, e.getMessage());
            } catch (IOException ex) {
                log.error("getOwner IOException");
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try (Scanner scanner = new Scanner(req.getInputStream(), StandardCharsets.UTF_8)) {
            String jsonData = scanner.useDelimiter("\\A").next();
            InputOwnerDto newOwner = new Gson().fromJson(jsonData, InputOwnerDto.class);

            log.info("added owner");

            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            ownerService.addOwner(newOwner, resp);
        } catch (IOException | RuntimeException e) {
            log.error("postOwner Exception");
            e.printStackTrace();
            try {
                resp.sendError(500, e.getMessage());
            } catch (IOException ex) {
                log.error("postOwner IOException");
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        long id;
        try {
            id = Long.parseLong(req.getParameter("id"));
        } catch (RuntimeException e) {
            log.error("getOwner Exception");
            e.printStackTrace();
            resp.setStatus(400);
            return;
        }
        resp = ownerService.deleteOwnerById(id, resp);

        log.info("deleted status={} ownerId={}", resp.getStatus(), id);
    }
}
