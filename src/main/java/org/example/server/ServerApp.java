package org.example.server;

import org.example.common.util.LoggerUtil;
import org.example.server.commands.*;
import org.example.server.handlers.RequestHandler;
import org.example.server.manager.CollectionManager;
import org.example.server.manager.CommandManager;
import org.example.server.network.ServerNetworkManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerApp {
    private static final int port = 12345;
    private static final Logger logger = LoggerUtil.getLogger();

    public static void main(String[] args){
        LoggerUtil.initLogger("server.log");
        String filename = System.getenv("XML_FILENAME");
        if (filename == null || filename.isEmpty()){
            filename = "data.xml";
            logger.info("Переменная окружения XML_FILENAME не задана, используем файл data.xml");
        }


        CollectionManager collectionManager = new CollectionManager();

        CommandManager commandManager = new CommandManager(){{
            register("help", new Help());
            register("add", new Add(collectionManager));
            register("show", new Show(collectionManager));
            register("show", new Show(collectionManager));
            register("info", new Info(collectionManager));
            register("history", new History(this));
            register("clear", new Clear(collectionManager));
            register("print_descending", new PrintDescending(collectionManager));
            register("print_field_descending_type", new PrintFieldDescendingType(collectionManager));
            register("remove_head", new RemoveHead(collectionManager));
            register("remove_greater", new RemoveGreater(collectionManager));
            register("remove_by_id", new RemoveById(collectionManager));
            register("remove_any_by_official_address", new RemoveAnyByOfficialAddress(collectionManager));
            register("update", new Update(collectionManager));
        }};

        RequestHandler handler = new RequestHandler(commandManager);

        Runtime.getRuntime().addShutdownHook(new Thread(collectionManager::saveCollection));

        try {
            ServerNetworkManager server = new ServerNetworkManager(port);
            server.start(handler);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка запуска сервера: " + e.getMessage(), e);
            System.out.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
