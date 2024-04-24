package ru.aston.hw3.developer.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hw3.developer.model.dto.InputDeveloperDto;
import ru.aston.hw3.developer.service.DeveloperService;
import ru.aston.hw3.testUtils.MockServletInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeveloperControllerTest {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    @Mock
    private DeveloperService developerService;

    @InjectMocks
    private DeveloperController developerController;

    @Test
    void testDoGet_WhenRequestIsNotBad_ThenReturnDeveloper() {
        var id = 1243L;
        var paramShort = true;
        HttpServletResponse responseResult = mock(HttpServletResponse.class);
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(request.getParameter("short")).thenReturn(String.valueOf(paramShort));
        when(developerService.getDeveloperById(id, paramShort, response)).thenReturn(responseResult);

        developerController.doGet(request, response);

        verify(request).getParameter("id");
        verify(request, times(2)).getParameter("short");
        verify(developerService).getDeveloperById(id, paramShort, response);
    }

    @Test
    void testDoGet_WhenRequestHasBadId_ThenError500() throws IOException {
        when(request.getParameter("id")).thenReturn("Конь");
        developerController.doGet(request, response);
        verify(response).sendError(eq(500), anyString());
    }

    @Test
    void testDoPost_WhenNotBadResponse() throws IOException {
        String s = """
                {
                \t"firstName": "12",
                \t"lastName": "11",
                \t"email": "mail"
                }""";
        when(request.getInputStream()).thenReturn(new MockServletInputStream(s));

        developerController.doPost(request, response);

        verify(developerService).addDeveloper(any(InputDeveloperDto.class), eq(response));
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("utf-8");
    }

    @Test
    void testDoPost_ExceptionInTryBlock() throws IOException {
        when(request.getInputStream()).thenThrow(new RuntimeException("Simulated RuntimeException"));

        developerController.doPost(request, response);

        verify(response).sendError(500, "Simulated RuntimeException");
    }

    @Test
    void testDoDelete_WhenRequestIsOk() {
        var id = 345L;
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(developerService.deleteDeveloperById(id, response)).thenReturn(response);

        developerController.doDelete(request, response);

        verify(developerService).deleteDeveloperById(id, response);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDelete_WhenException_ThenReturnStatus400() {
        String errMessage = "Simulated NumberFormatException";
        when(request.getParameter("id")).thenThrow(new NumberFormatException(errMessage));

        developerController.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}