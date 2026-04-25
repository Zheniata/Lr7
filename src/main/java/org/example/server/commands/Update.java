package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;

public class Update extends Command{
    CollectionManager collectionManager;
    public Update(CollectionManager collectionManager){
        super("update");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            long id;
            try {

                Object arg = request.getArgument();
                if (arg == null) {
                    return new Response(false, "Укажите id организации", null);
                }

                id = Long.parseLong(arg.toString().trim());
            } catch (NumberFormatException e) {
                return new Response(false, "id должен быть целым числом", null);
            }

            Organization oldOrg = collectionManager.getById(id);
            if (oldOrg == null) {
                return new Response(false, "Организация с id=" + id + " не найдена", null);
            }

            if (!collectionManager.isOwner(id, user.getId())) {
                return new Response(false, "Нет прав на обновление этой организации", null);
            }



            Organization newOrg = request.getOrganization();
            if (newOrg == null) {
                return new Response(false, "Нет данных для обновления", null);
            }

            newOrg.setId(oldOrg.getId());
            newOrg.setCreationDate(oldOrg.getCreationDate());
            newOrg.setOwnerId(oldOrg.getOwnerId());

            boolean success = collectionManager.update(newOrg);
            if (success) {
                return new Response(true, "Организация с id=" + id + " обновлена", null);
            } else {
                return new Response(false, "Ошибка обновления", null);
            }

        } catch (ClassCastException e) {
            return new Response(false, "Ошибка типа данных: " + e.getMessage(), null);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
}
