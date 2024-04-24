package ru.aston.hw3.solution.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ru.aston.hw3.solution.model.dto.InputSolutionLinkDto;
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
@WebServlet("/solutions/link")
public class SolutionLinkController extends HttpServlet {
    private SolutionService solutionService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.solutionService = new SolutionServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try (Scanner scanner = new Scanner(req.getInputStream(), StandardCharsets.UTF_8)) {
            String jsonData = scanner.useDelimiter("\\A").next();
            InputSolutionLinkDto newSolutionLink = new Gson().fromJson(jsonData, InputSolutionLinkDto.class);

            log.info("add solutionLink={}", newSolutionLink);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");
            if (solutionService.addSolutionLink(newSolutionLink)) {
                resp.setStatus(200);
            } else {
                resp.setStatus(400);
            }
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
}

