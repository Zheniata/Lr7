package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.common.models.Organization;
import org.example.server.manager.CollectionManager;

import java.sql.SQLException;
import java.time.LocalDate;

public class Add extends Command{
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager){
        super("add");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            Organization org = request.getOrganization();
            if (org == null){
                return new Response(false, "Ошибка при передачи организации", null);
            }

            boolean success = collectionManager.add(org, user.getId());
            if (success){
                return new Response(true, "Организация добавлена", null);
            } else {
                return new Response(false, "Ошибка добавления", null);
            }
        } catch (Exception e){
            return new Response(false, "Произошла ошибка: " + e.getMessage(), null);
        }
    }
}
