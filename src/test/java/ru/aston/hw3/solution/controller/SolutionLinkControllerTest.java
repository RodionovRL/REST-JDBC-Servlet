package ru.aston.hw3.solution.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hw3.solution.model.dto.InputSolutionLinkDto;
import ru.aston.hw3.solution.service.SolutionService;
import ru.aston.hw3.testUtils.MockServletInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionLinkControllerTest {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    @Mock
    private SolutionService solutionService;

    @InjectMocks
    private SolutionLinkController solutionLinkController;

    @Test
    void testDoPost_WhenNotBadResponseAndResultIsTrue_ThenStatusIs200() throws IOException {
        String s = """
                {
                \t"solutionId": "34",
                \t"ownerId": "45",
                \t"developerId": "56"
                }""";
        Boolean result = true;
        int requiredStatus = HttpServletResponse.SC_OK;
        when(request.getInputStream()).thenReturn(new MockServletInputStream(s));
        when(solutionService.addSolutionLink(any(InputSolutionLinkDto.class))).thenReturn(result);

        solutionLinkController.doPost(request, response);

        verify(solutionService).addSolutionLink(any(InputSolutionLinkDto.class));
        verify(response).setStatus(requiredStatus);
    }

    @Test
    void testDoPost_WhenNotBadResponseAndResultIsFalse_ThenStatusIs400() throws IOException {
        String s = """
                {
                \t"solutionId": "34",
                \t"ownerId": "45",
                \t"developerId": "56"
                }""";
        Boolean result = false;
        int requiredStatus = HttpServletResponse.SC_BAD_REQUEST;
        when(request.getInputStream()).thenReturn(new MockServletInputStream(s));
        when(solutionService.addSolutionLink(any(InputSolutionLinkDto.class))).thenReturn(result);

        solutionLinkController.doPost(request, response);

        verify(solutionService).addSolutionLink(any(InputSolutionLinkDto.class));
        verify(response).setStatus(requiredStatus);
    }

    @Test
    void testDoPost_ExceptionInTryBlock() throws IOException {
        when(request.getInputStream()).thenThrow(new RuntimeException("Simulated RuntimeException"));

        solutionLinkController.doPost(request, response);

        verify(response).sendError(500, "Simulated RuntimeException");
    }
}