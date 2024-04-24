package ru.aston.hw3.owner.service;

import ru.aston.hw3.owner.model.dto.InputOwnerDto;

import javax.servlet.http.HttpServletResponse;

public interface OwnerService {
    /**
     * Метод добавления нового владельца продукта.
     * @param inputOwnerDto - входное DTO с данными нового владельца продукта.
     * @param resp          - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse addOwner(InputOwnerDto inputOwnerDto, HttpServletResponse resp);

    /**
     * Метод получения владельца продукта по ID
     * @param id     - ID запрашиваемого владельца продукта.
     * @param isLazy - полнота запрашиваемых данных.
     *               True - получить данные без связанных сущностей.
     *               False - получить данные вместе со связанными сущностями.
     * @param resp   - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse getOwnerById(long id, boolean isLazy, HttpServletResponse resp);

    /**
     * Метод удаления владельца продукта по ID
     * @param id   - ID удаляемого владельца продукта.
     * @param resp - HttpServletResponse-упаковка для данных, которые будут отправлены в ответ на запрос.
     * @return - HttpServletResponse-упаковка с данными, которые будут отправлены в ответ на запрос.
     */
    HttpServletResponse deleteOwnerById(long id, HttpServletResponse resp);
}
