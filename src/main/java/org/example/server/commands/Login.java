package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.server.manager.AuthManager;

public class Login extends Command{
    private AuthManager authManager;

    public Login(AuthManager authManager){
        super("login");
        this.authManager = authManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            String argument = request.getArgument();
            String[] args = argument != null ? argument.split(" ") : new String[0];

            if (args == null || args.length < 2) {
                return new Response(false, "Недостаточно аргументов", null);
            }

            String login = args[0];
            String password = args[1];

            User authenticatedUser = authManager.authenticate(login, password);
            if (authenticatedUser != null) {
                return new Response(true, "Авторизация успешна", null, authenticatedUser);
            } else {
                return new Response(false, "Неверный логин или пароль", null);
            }

        } catch (Exception e) {
            return new Response(false, "Ошибка авторизации: " + e.getMessage(), null);
        }
    }
}
