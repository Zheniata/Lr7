package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.server.manager.CollectionManager;

public class Clear extends Command{
    CollectionManager collectionManager;
    public Clear(CollectionManager collectionManager){
        super("clear");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
         try {
             collectionManager.clear();
             return new Response(true, "Коллекция очищена", null);
         } catch (Exception e) {
             return new Response(false, "Произошла ошибка: " + e.getMessage(), null);
         }
    }
}
