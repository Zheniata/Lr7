package org.example.server;

import org.example.server.commands.*;
import org.example.server.handlers.RequestHandler;
import org.example.server.manager.AuthManager;
import org.example.server.manager.CollectionManager;
import org.example.server.manager.CommandManager;
import org.example.server.manager.DatabaseManager;
import org.example.server.network.ServerNetworkManager;

import java.sql.SQLException;

public class ServerApp {
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final int port = 12345;


    public static void main(String[] args) {
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            System.err.println("Ошибка: не заданы переменные окружения!");
            System.exit(1);

            System.out.println("URL из env: " + DB_URL);
            System.out.println("User из env: " + DB_USER);
            System.out.println("Password из env: " + (DB_PASSWORD != null ? "****" : "NULL"));
        }

        try {
            DatabaseManager databaseManager = new DatabaseManager(DB_URL, DB_USER, DB_PASSWORD);

            CollectionManager collectionManager = new CollectionManager(databaseManager);

            AuthManager authManager = new AuthManager(databaseManager);

            CommandManager commandManager = new CommandManager(collectionManager, authManager);

            RequestHandler requestHandler = new RequestHandler(commandManager);

            ServerNetworkManager server = new ServerNetworkManager(port);
            System.out.println("Сервер запущен на порту " + port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Завершение работы сервера...");
                if (server != null) {
                    server.stop();
                }
            }));

            server.start(requestHandler);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
        }

    }
}