package ru.aston.hw3.owner.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hw3.owner.mapper.OwnerMapper;
import ru.aston.hw3.owner.model.Owner;
import ru.aston.hw3.owner.model.dto.InputOwnerDto;
import ru.aston.hw3.owner.service.OwnerService;
import ru.aston.hw3.testUtils.MockServletInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest extends Mockito {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private OwnerController ownerController;

    @Test
    void testDoGet_WhenRequestIsNotBad_ThenReturnOwner() {
        var id = 1243L;
        var paramShort = true;
        HttpServletResponse responseResult = mock(HttpServletResponse.class);
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(request.getParameter("short")).thenReturn(String.valueOf(paramShort));
        when(ownerService.getOwnerById(id, paramShort, response)).thenReturn(responseResult);

        ownerController.doGet(request, response);

        verify(request).getParameter("id");
        verify(request, times(2)).getParameter("short");
        verify(ownerService).getOwnerById(id, paramShort, response);
    }

    @Test
    void testDoGet_WhenRequestHasBadId_ThenError500() throws IOException {
        when(request.getParameter("id")).thenReturn("Конь");
        ownerController.doGet(request, response);
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

        ownerController.doPost(request, response);

        verify(ownerService).addOwner(any(InputOwnerDto.class), eq(response));
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("utf-8");
    }

    @Test
    void testDoPost_ExceptionInTryBlock() throws IOException {
        when(request.getInputStream()).thenThrow(new RuntimeException("Simulated RuntimeException"));

        ownerController.doPost(request, response);

        verify(response).sendError(500, "Simulated RuntimeException");
    }

    @Test
    void testDoDelete_WhenRequestIsOk() {
        var id = 345L;
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        when(request.getParameter("id")).thenReturn(String.valueOf(id));
        when(ownerService.deleteOwnerById(id, response)).thenReturn(response);

        ownerController.doDelete(request, response);

        verify(ownerService).deleteOwnerById(id, response);
        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void testDoDelete_WhenException_ThenReturnStatus400() {
        String errMessage = "Simulated NumberFormatException";
        when(request.getParameter("id")).thenThrow(new NumberFormatException(errMessage));

        ownerController.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}