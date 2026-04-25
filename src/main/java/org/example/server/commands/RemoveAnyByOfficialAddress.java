package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Address;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;
import java.util.List;

public class RemoveAnyByOfficialAddress extends Command{
    CollectionManager collectionManager;
    public RemoveAnyByOfficialAddress(CollectionManager collectionManager){
        super("remove_any_by_official_address");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            Object arg = request.getArgument();
            String street = null;

            if (arg != null) {
                street = (String) arg;
            }

            if (street == null || street.trim().isEmpty()) {
                return new Response(false, "Укажите название улицы", null);
            }

            street = street.trim();

            List<Organization> collection = collectionManager.getAll();
            if (collection.isEmpty()) {
                return new Response(true, "Коллекция пуста", null);
            }
            Organization toRemove = null;
            for (Organization org : collection) {
                Address addr = org.getOfficialAddress();
                if (addr != null && street.equals(addr.getStreet())) {
                    if (!collectionManager.isOwner(org.getId(), user.getId())) {
                        return new Response(false, "Нет прав на удаление этой организации", null);
                    }

                    toRemove = org;
                    break;
                }
            }
            if (toRemove == null) {
                return new Response(true, "Элемент с улицей: " + street + " не найден", null);
            } else {
                boolean success = collectionManager.removeById(toRemove.getId());
                if (success) {
                    return new Response(true, "Элемент с адресом: " + street + " удалён", null);
                } else {
                    return new Response(false, "Ошибка удаления", null);
                }
            }
        } catch (Exception e) {
            return new Response(false, "Произошла ошибка: " + e.getMessage(), null);
        }
    }
}
