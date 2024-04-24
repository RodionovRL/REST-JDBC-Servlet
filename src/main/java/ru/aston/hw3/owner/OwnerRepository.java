package ru.aston.hw3.owner;

import ru.aston.hw3.owner.model.Owner;

import java.util.Optional;

public interface OwnerRepository {
    /**
     * Добавление новой записи о владельце продукта в репозиторий
     *
     * @param newOwner - сущность Владелец продукта
     * @return - Optional-обёртка с добавленной записью
     */
    Optional<Owner> addOwner(Owner newOwner);

    /**
     * Получение из репозитория записи Владелец продукта по ID
     *
     * @param id     - ID запрашиваемой записи
     * @param isLazy - полнота запрашиваемых данных.
     *               True - получить данные без связанных сущностей.
     *               False - получить данные вместе со связанными сущностями
     * @return - Optional-обёртка с запрашиваемой записью
     */
    Optional<Owner> getOwner(long id, boolean isLazy);

    /**
     * Удаление из репозитория записи Владелец продукта по ID
     *
     * @param id - ID удаляемой записи
     * @return - int число удалённых записей
     */
    int deleteOwner(long id);
}
