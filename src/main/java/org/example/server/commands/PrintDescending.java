package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrintDescending extends Command{
    CollectionManager collectionManager;
    public PrintDescending(CollectionManager collectionManager){
        super("print_descending");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try{
            String result = collectionManager.sortDescending();
            return new Response(true, "Элементы коллекции в порядке убывания", result);
        } catch (Exception e) {
            return new Response(false, "Произошла ошибка: " + e.getMessage(), null);
        }

    }
}
