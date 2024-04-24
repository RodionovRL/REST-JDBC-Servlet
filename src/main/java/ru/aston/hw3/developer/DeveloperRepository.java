package ru.aston.hw3.developer;

import ru.aston.hw3.developer.model.Developer;

import java.util.Optional;

public interface DeveloperRepository {
    /**
     * Добавление новой записи о разработчике в репозиторий
     *
     * @param newDeveloper - сущность Разработчик
     * @return - Optional-обёртка с добавленной записью
     */
    Optional<Developer> addDeveloper(Developer newDeveloper);

    /**
     * Получение из репозитория записи Разработчик по ID
     *
     * @param id     - ID запрашиваемой записи
     * @param isLazy - полнота запрашиваемых данных.
     *               True - получить данные без связанных сущностей.
     *               False - получить данные вместе со связанными сущностями
     * @return - Optional-обёртка с запрашиваемой записью
     */
    Optional<Developer> getDeveloper(long id, boolean isLazy);

    /**
     * Удаление из репозитория записи Разработчик по ID
     *
     * @param id - ID удаляемой записи
     * @return - int число удалённых записей
     */
    int deleteDeveloper(long id);
}
