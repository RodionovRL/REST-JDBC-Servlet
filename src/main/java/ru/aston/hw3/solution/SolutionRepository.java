package ru.aston.hw3.solution;

import ru.aston.hw3.solution.model.Solution;

import java.util.Optional;

public interface SolutionRepository {
    /**
     * Добавление новой записи о проекте в репозиторий
     *
     * @param newSolution - сущность Проект
     * @return - Optional-обёртка с добавленной записью
     */
    Optional<Solution> addSolution(Solution newSolution);

    /**
     * Получение из репозитория записи Проект по ID
     *
     * @param id     - ID запрашиваемой записи
     * @param isLazy - полнота запрашиваемых данных.
     *               True - получить данные без связанных сущностей.
     *               False - получить данные вместе со связанными сущностями
     * @return - Optional-обёртка с запрашиваемой записью
     */
    Optional<Solution> getSolution(long id, boolean isLazy);

    /**
     * Удаление из репозитория записи Проект по ID
     *
     * @param id - ID удаляемой записи
     * @return - int число удалённых записей
     */
    int deleteSolution(long id);

    /**
     * Добавление связи Проект - Разработчик в репозиторий.
     *
     * @param solutionId  - ID проекта
     * @param developerId - ID разработчика
     * @return - boolean результат выполнения:
     * True - информация добавлена в репозиторий;
     * False - добавление информации не произошло.
     */
    boolean setSolutionDeveloper(Long solutionId, Long developerId);

    /**
     * Добавление связи Проект - Владелец продукта в репозиторий.
     *
     * @param solutionId - ID проекта
     * @param ownerId    - ID владельца продукта
     * @return - boolean результат выполнения:
     * True - информация добавлена в репозиторий;
     * False - добавление информации не произошло.
     */
    boolean setSolutionOwner(Long solutionId, Long ownerId);
}
