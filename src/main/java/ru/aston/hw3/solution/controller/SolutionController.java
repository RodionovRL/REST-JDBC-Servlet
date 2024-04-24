package ru.aston.hw3.solution.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.aston.hw3.solution.model.dto.InputSolutionDto;
import ru.aston.hw3.solution.service.SolutionService;
import ru.aston.hw3.solution.service.SolutionServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
@WebServlet("/solutions")
public class SolutionController extends HttpServlet {
    private SolutionService solutionService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.solutionService = new SolutionServiceImpl();
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

            log.info("get Solution by id={} isLazy={}", id, isLazy);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            solutionService.getSolutionById(id, isLazy, resp);
        } catch (RuntimeException e) {
            log.error("getSolution exception");
            e.printStackTrace();
            try {
                resp.sendError(500, e.getMessage());
            } catch (IOException ex) {
                log.error("getSolutions IOException");
                ex.printStackTrace();
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try (Scanner scanner = new Scanner(req.getInputStream(), StandardCharsets.UTF_8)) {
            String jsonData = scanner.useDelimiter("\\A").next();
            InputSolutionDto newSolution = new Gson().fromJson(jsonData, InputSolutionDto.class);

            log.info("add solution={}", newSolution);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            solutionService.addSolution(newSolution, resp);
        } catch (IOException | RuntimeException e) {
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
            log.error("deleteSolution Exception");
            e.printStackTrace();
            resp.setStatus(400);
            return;
        }
        resp = solutionService.deleteSolutionById(id, resp);

        log.info("deleted status={}, solutionsId={}", resp.getStatus(), id);
    }
}
