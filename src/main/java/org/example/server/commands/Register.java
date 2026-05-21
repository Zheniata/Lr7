package org.example.server.commands;

import org.example.common.Request;
import org.example.common.Response;
import org.example.common.User;
import org.example.server.manager.AuthManager;

public class Register extends Command{
    private AuthManager authManager;

    public Register(AuthManager authManager){
        super("register");
        this.authManager = authManager;
    }

    @Override
    public Response execute(Request request, User user) {
        try {
            String argument = request.getArgument();
            String[] args = argument != null ? argument.split(" ") : new String[0];

            if (args.length < 2) {
                return new Response(false, "Недостаточно аргументов", null);
            }

            String login = args[0];
            String password = args[1];

            User newUser = authManager.register(login, password);

            if (newUser != null) {
                return new Response(true, "Пользователь зарегистрирован", null, newUser);
            } else {
                return new Response(false, "Пользователь уже существует", null);
            }
        } catch (Exception e) {
            System.err.println("❌ Ошибка регистрации: " + e.getMessage());
            e.printStackTrace();
            return new Response(false, "Ошибка регистрации: " + e.getMessage(), null);
        }
    }
}
