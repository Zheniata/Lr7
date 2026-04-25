package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class RemoveGreater extends Command{
    CollectionManager collectionManager;
    public RemoveGreater(CollectionManager collectionManager){
        super("remove_greater");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            String argument = request.getArgument().toString();

            long idMax;
            try {
                idMax = Long.parseLong(argument.trim());
            } catch (NumberFormatException e) {
                return new Response(false, "id должен быть целым числом", null);
            }
            List<Organization> collection = collectionManager.getAll();

            if (collection.isEmpty()) {
                return new Response(true, "Коллекция пуста", null);
            }

            int removedCount = 0;

            for (Organization org: collection){
                if (org.getId() > idMax){
                    if (!collectionManager.isOwner(org.getId(), user.getId())) {
                        continue;
                    }

                    boolean success = collectionManager.removeById(org.getId());
                    if (success) {
                        removedCount++;
                    }
                }
            }

            if (removedCount == 0) {
                return new Response(true, "Нет элементов, которые превышают id=" + idMax, null);
            } else {
                return new Response(true, "Элементы, превышающие id=" + idMax + " удалены", null);
            }


        } catch (Exception e) {
            return new Response(false, "Произошла ошибка: " + e.getMessage(), null);
        }
    }
}
