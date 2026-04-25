package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.server.manager.CommandManager;

import java.util.List;

public class History extends Command{
    CommandManager commandManager;
    public History(CommandManager commandManager){
        super("history");
        this.commandManager = commandManager;
    }

    @Override
    public Response execute(Request request, User user) {
        List<String> history = commandManager.getCommandHistory();

        if (history.isEmpty()) {
            return new Response(true, "История команд пуста", null);
        }

        String result = "";
        for (String i : history) {
            result += i + "\n";
        }

        return new Response(true, result, null);
        }
    }
