package org.example.server.manager;

import org.example.server.commands.Command;

import java.nio.file.Path;
import java.util.*;

public class CommandManager {
    private Map<String, Command> commands = new HashMap<>();
    private List<String> commandHistory = new ArrayList<>();
    private final Set<Path> executingScripts = new HashSet<>();

    /**
     * Регистрирует новую команду в менеджере.
     *
     * @param name    имя команды (должно совпадать с тем, что пользователь вводит в консоли)
     * @param command экземпляр команды
     */

    public void register(String name, Command command){
        commands.put(name, command);
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

    /**
     * Проверяет, выполняется ли в данный момент скрипт по указанному пути.
     *
     * @param path путь к файлу скрипта
     * @return {@code true}, если скрипт уже выполняется; иначе {@code false}
     */

    public boolean isExecuteScript(Path path){
        return executingScripts.contains(path);
    }

    /**
     * Добавляет путь к скрипту в множество выполняемых.
     *
     * @param path путь к файлу скрипта
     */

    public void addToExecuteScript(Path path){
        executingScripts.add(path);
    }

    /**
     * Удаляет путь к скрипту из множества выполняемых
     *
     * @param path путь к файлу скрипта
     */

    public void removeFromExecuteScript(Path path){
        executingScripts.remove(path);
    }
}
