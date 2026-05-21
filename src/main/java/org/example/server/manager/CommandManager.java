package org.example.server.manager;

import org.example.server.commands.*;
import org.example.server.commands.Command;

import java.nio.file.Path;
import java.util.*;

public class CommandManager {
    private Map<String, Command> commands = new HashMap<>();
    private List<String> commandHistory = new ArrayList<>();
    private final Set<Path> executingScripts = new HashSet<>();

    public CommandManager(CollectionManager collectionManager, AuthManager authManager){
        commands.put("help", new Help());
        commands.put("info", new Info(collectionManager));
        commands.put("show", new Show(collectionManager));
        commands.put("add", new Add(collectionManager));
        commands.put("update", new Update(collectionManager));
        commands.put("remove_by_id", new RemoveById(collectionManager));
        commands.put("clear", new Clear(collectionManager));
        commands.put("remove_head", new RemoveHead(collectionManager));
        commands.put("remove_greater", new RemoveGreater(collectionManager));
        commands.put("remove_any_by_official_address", new RemoveAnyByOfficialAddress(collectionManager));
        commands.put("print_descending", new PrintDescending(collectionManager));
        commands.put("print_field_descending_type", new PrintFieldDescendingType(collectionManager));
        commands.put("history", new History(this));
        commands.put("login", new Login(authManager));
        commands.put("register", new Register(authManager));
    }

    /**
     * Возвращает команду по её имени.
     *
     * @param name имя команды
     * @return экземпляр команды или {@code null}, если не найдена
     */

    public Command getCommand(String name) {
        return commands.get(name);
    }

    /**
     * Возвращает все зарегистрированные команды.
     *
     * @return карта "имя → команда"
     */

    public Map<String, Command> getCommands(){
        return commands;
    }

    /**
     * Возвращает историю последних выполненных команд (максимум 6).
     *
     * @return список имён команд
     */

    public List<String> getCommandHistory(){
        return commandHistory;
    }

    /**
     * Добавляет команду в историю.
     * Если история превышает 6 элементов, удаляется самый старый.
     *
     * @param command имя команды
     */

    public void addToCommandHistory(String command){
        commandHistory.add(command);
        if (commandHistory.size() > 6){
            commandHistory.remove(0);
        }
    }
}