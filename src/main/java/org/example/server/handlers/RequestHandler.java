package org.example.server.handlers;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.server.commands.Command;
import org.example.server.manager.AuthManager;
import org.example.server.manager.CommandManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestHandler {
    private final CommandManager commandManager;
    private final Map<String, User> userSessions = new HashMap<>();
    private static final Set<String> NO_AUTH_COMMANDS = Set.of("login", "register", "help");

    public RequestHandler(CommandManager commandManager){
        this.commandManager = commandManager;
    }

    public Response handle(Request request, String clientId) {
        String commandName = request.getName();

        if (!NO_AUTH_COMMANDS.contains(commandName)) {
            User user = userSessions.get(clientId);

            if (user == null) {
                return new Response(false, "Для выполнения этой команды требуется авторизация", null);
            }

            return executeCommand(commandName, request, user);
        }

        Response response = executeCommand(commandName, request, null);

        if ((commandName.equals("login") || commandName.equals("register")) && response.isSuccess()) {
            userSessions.put(clientId, response.getUser());
        }

        return response;
    }


    public User getCurrentUser(String clientId) {
        return userSessions.get(clientId);
    }

    private Response executeCommand(String commandName, Request request, User user) {
        Command command = commandManager.getCommand(commandName);

        if (command == null) {
            return new Response(false, "Неизвестная команда: " + commandName, null);
        }

        commandManager.addToCommandHistory(commandName);

        return command.execute(request, user);
    }

    public void removeSession(String clientId) {
        userSessions.remove(clientId);
    }

    public boolean isAuthenticated(String clientId) {
        return userSessions.containsKey(clientId);
    }
}
