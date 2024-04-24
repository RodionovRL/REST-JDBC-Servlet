package ru.aston.hw3.developer.service;

import ru.aston.hw3.developer.model.dto.InputDeveloperDto;

import javax.servlet.http.HttpServletResponse;

public interface DeveloperService {
    /**
     * Метод добавления нового разработчика.
     *
     * @param inputDeveloperDto -  входное DTO с данными нового разработчика.
     * @param resp              - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse addDeveloper(InputDeveloperDto inputDeveloperDto, HttpServletResponse resp);

    /**
     * Метод получения разработчика по ID
     *
     * @param id     - ID запрашиваемого разработчика.
     * @param isLazy - полнота запрашиваемых данных.
     *               True - получить данные без связанных сущностей.
     *               False - получить данные вместе со связанными сущностями.
     * @param resp   - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse getDeveloperById(long id, boolean isLazy, HttpServletResponse resp);

    /**
     * Метод удаления разработчика по ID
     *
     * @param id   - ID удаляемого разработчика.
     * @param resp - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse deleteDeveloperById(long id, HttpServletResponse resp);
}
