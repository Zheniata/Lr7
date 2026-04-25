package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;

import java.util.Collection;

public class Show extends Command{
    CollectionManager collectionManager;

    public Show(CollectionManager collectionManager){
        super("show");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            Collection<Organization> collection = collectionManager.getCollection();

            if (collection.isEmpty()) {
                return new Response(true, "Коллекция пуста", null);
            }

            String result = collectionManager.sort();

            int size = collectionManager.size();

            return new Response(true, "В коллекции " + size + " элемент/ов, отсортированный по id", result);
        } catch (Exception e){
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
}
