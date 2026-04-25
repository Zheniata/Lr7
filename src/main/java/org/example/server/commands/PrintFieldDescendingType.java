package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrintFieldDescendingType extends Command{
    CollectionManager collectionManager;
    public PrintFieldDescendingType(CollectionManager collectionManager){
        super("print_field_descending_type");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            String result = collectionManager.sortFieldDescendingType();
            return new Response(true, "Поле type в порядке убывания id", result);
        } catch (Exception e){
            return new Response(false, "Произошла ошибка: " + e.getMessage(), null);
        }
    }
}
