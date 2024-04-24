package ru.aston.hw3.solution.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hw3.solution.model.dto.InputSolutionDto;
import ru.aston.hw3.solution.service.SolutionService;
import ru.aston.hw3.testUtils.MockServletInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolutionControllerTest {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    @Mock
    private SolutionService solutionService;

    @InjectMocks
    private SolutionController solutionController;

    @Test
    void testDoGet_WhenRequestIsNotBad_ThenReturnSolution() {
        var id = 1243L;
        var paramShort = true;
        HttpServletResponse responseResult = mock(HttpServletResponse.class);
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(request.getParameter("short")).thenReturn(String.valueOf(paramShort));
        when(solutionService.getSolutionById(id, paramShort, response)).thenReturn(responseResult);

        solutionController.doGet(request, response);

        verify(request).getParameter("id");
        verify(request, times(2)).getParameter("short");
        verify(solutionService).getSolutionById(id, paramShort, response);
    }

    @Test
    void testDoGet_WhenRequestHasBadId_ThenError500() throws IOException {
        when(request.getParameter("id")).thenReturn("Конь");
        solutionController.doGet(request, response);
        verify(response).sendError(eq(500), anyString());
    }

    @Test
    void testDoPost_WhenNotBadResponse() throws IOException {
        String s = """
                {
                \t"name": "12",
                \t"version": "11"
                }""";
        when(request.getInputStream()).thenReturn(new MockServletInputStream(s));

        solutionController.doPost(request, response);

        verify(solutionService).addSolution(any(InputSolutionDto.class), eq(response));
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("utf-8");
    }

    @Test
    void testDoPost_ExceptionInTryBlock() throws IOException {
        when(request.getInputStream()).thenThrow(new RuntimeException("Simulated RuntimeException"));

        solutionController.doPost(request, response);

        verify(response).sendError(500, "Simulated RuntimeException");
    }

    @Test
    void testDoDelete_WhenRequestIsOk() {
        var id = 345L;
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(solutionService.deleteSolutionById(id, response)).thenReturn(response);

        solutionController.doDelete(request, response);

        verify(solutionService).deleteSolutionById(id, response);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDelete_WhenException_ThenReturnStatus400() {
        String errMessage = "Simulated NumberFormatException";
        when(request.getParameter("id")).thenThrow(new NumberFormatException(errMessage));

        solutionController.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}