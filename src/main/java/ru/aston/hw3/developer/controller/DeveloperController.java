package ru.aston.hw3.developer.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.aston.hw3.developer.model.dto.InputDeveloperDto;
import ru.aston.hw3.developer.service.DeveloperService;
import ru.aston.hw3.developer.service.DeveloperServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
@WebServlet("/developers/*")
public class DeveloperController extends HttpServlet {
    private DeveloperService developerService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.developerService = new DeveloperServiceImpl();
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

            log.info("get Developer by id={}", id);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            developerService.getDeveloperById(id, isLazy, resp);
        } catch (RuntimeException e) {
            log.error("getDeveloper exception");
            e.printStackTrace();
            try {
                resp.sendError(500, e.getMessage());
            } catch (IOException ex) {
                log.error("getDeveloper IOException");
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try (Scanner scanner = new Scanner(req.getInputStream(), StandardCharsets.UTF_8)) {

            String jsonData = scanner.useDelimiter("\\A").next();
            InputDeveloperDto newDeveloper = new Gson().fromJson(jsonData, InputDeveloperDto.class);

            log.info("add developer={}", newDeveloper);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            developerService.addDeveloper(newDeveloper, resp);
        } catch (RuntimeException | IOException e) {
            log.error("JsonSyntaxException | IOException");
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        long id;
        try {
            id = Long.parseLong(req.getParameter("id"));
        } catch (RuntimeException e) {
            log.error("getDeveloper Exception");
            e.printStackTrace();
            resp.setStatus(400);
            return;
        }
        resp = developerService.deleteDeveloperById(id, resp);

        log.info("deleted status={} developerId={}", resp.getStatus(), id);
    }
}
