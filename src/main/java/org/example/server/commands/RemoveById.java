package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;

public class RemoveById extends Command {
    CollectionManager collectionManager;
    public RemoveById(CollectionManager collectionManager){
        super("remove_by_id");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            String argument = request.getArgument().toString();

            long id;
            try {
                id = Long.parseLong(argument.trim());
            } catch (NumberFormatException e) {
                return new Response(false, "id должен быть целым числом", null);
            }
            Organization organization = collectionManager.getById(id);
            if (organization == null) {
                return new Response(false, "Организация с id=" + id + " не найдена", null);
            }

            if (!collectionManager.isOwner(id, user.getId())) {
                return new Response(false, "Нет прав на удаление этой организации", null);
            }
            boolean success = collectionManager.removeById(id);
            if (success) {
                return new Response(true, "Организация с id=" + id + " удалена", null);
            } else {
                return new Response(false, "Ошибка удаления", null);
            }

        } catch (Exception e) {
            return new Response(false, "Произошла ошибка: " + e.getMessage(), null);
        }
    }
}
