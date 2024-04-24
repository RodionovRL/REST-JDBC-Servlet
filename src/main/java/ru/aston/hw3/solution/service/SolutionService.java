package ru.aston.hw3.solution.service;

import ru.aston.hw3.solution.model.dto.InputSolutionDto;
import ru.aston.hw3.solution.model.dto.InputSolutionLinkDto;

import javax.servlet.http.HttpServletResponse;

public interface SolutionService {
    /**
     * Метод добавления нового проекта.
     *
     * @param inputSolutionDto - входное DTO с данными нового проекта.
     * @param resp             - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse addSolution(InputSolutionDto inputSolutionDto, HttpServletResponse resp);

    /**
     * Метод получения проекта по ID
     *
     * @param id     - ID запрашиваемого проекта.
     * @param isLazy - полнота запрашиваемых данных.
     *               True - получить данные без связанных сущностей.
     *               False - получить данные вместе со связанными сущностями
     * @param resp   - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse getSolutionById(long id, boolean isLazy, HttpServletResponse resp);

    /**
     * Метод удаления проекта по ID
     *
     * @param id   - ID удаляемого проекта.
     * @param resp - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse deleteSolutionById(long id, HttpServletResponse resp);

    /**
     * Метод добавления проекту владельца и разработчиков
     *
     * @param newSolutionLink - входное DTO с информацией о добавляемых связях
     * @return True - если связи созданы,
     * False - если связь создать не удалось.
     */
    boolean addSolutionLink(InputSolutionLinkDto newSolutionLink);
}
