package org.example.server.handlers;

import org.example.common.Request;
import org.example.common.Response;
import org.example.server.commands.Command;
import org.example.server.manager.CommandManager;

public class RequestHandler {
    private final CommandManager commandManager;

    public RequestHandler(CommandManager commandManager){
        this.commandManager = commandManager;
    }

    public Response handle(Request request){
        String name = request.getName();
        Command command = commandManager.getCommand(name);

        if (command == null){
            return new Response(false, "Неизвестная команда: " + name, null);
        }

        try {
            commandManager.addToCommandHistory(name);
            return command.execute(request);
        } catch (Exception e) {
            return new Response(false, "Ошибка: " + e.getMessage(), null);
        }
    }
}
