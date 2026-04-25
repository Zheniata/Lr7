package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;

import java.util.PriorityQueue;

public class RemoveHead extends Command{
    CollectionManager collectionManager;
    public RemoveHead(CollectionManager collectionManager){
        super("remove_head");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try{
            Organization head = collectionManager.getAll().stream()
                    .min(Organization::compareTo)
                    .orElse(null);

            if (head == null){
                return new Response(true, "Коллекция пуста", null);
            }

            if (!collectionManager.isOwner(head.getId(), user.getId())){
                return new Response(false, "Нет прав на удаление", null);
            }

            boolean success = collectionManager.removeById(head.getId());
            if (success) {
                return new Response(true, "Первый элемент коллекции удалён:\n" + head, null);
            } else {
                return new Response(false, "Ошибка удаления первого элемента", null);
            }
        } catch (Exception e) {
            return new Response(false, "Произошла ошибка: " + e.getMessage(), null);
        }
    }
}
