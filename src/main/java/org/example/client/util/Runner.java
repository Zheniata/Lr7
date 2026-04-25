package org.example.client.util;

import org.example.client.commands.*;
import org.example.client.network.ClientNetworkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Runner {
    private final Scanner scanner;
    private final ClientNetworkManager networkManager;
    private final Map<String, Command> commands = new HashMap<>();

    public Runner(Scanner scanner, ClientNetworkManager networkManager) {
        this.scanner = scanner;
        this.networkManager = networkManager;

        commands.put("help", new Help(networkManager));
        commands.put("add", new Add(scanner, networkManager));
        commands.put("show", new Show(networkManager));
        commands.put("info", new Info(networkManager));
        commands.put("history", new History(networkManager));
        commands.put("clear", new Clear(networkManager));
        commands.put("print_descending", new PrintDescending(networkManager));
        commands.put("print_field_descending_type", new PrintFieldDescendingType(networkManager));
        commands.put("remove_head", new RemoveHead(networkManager));
        commands.put("remove_greater", new RemoveGreater(networkManager));
        commands.put("remove_by_id", new RemoveById(networkManager));
        commands.put("remove_any_by_official_address", new RemoveAnyByOfficialAddress(networkManager));
        commands.put("update", new Update(scanner, networkManager));
        commands.put("execute_script", new ExecuteScript(networkManager, scanner));
        commands.put("exit", new Exit());
    }

        public void interactiveMode() {
            System.out.println("Введите команду");
            while (true) {
                try {
                    System.out.println("> ");
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split(" ", 2);
                    String commandName = parts[0];
                    String argument = parts.length > 1 ? parts[1] : null;
                    Command command = commands.get(commandName);
                    if (command == null) {
                        System.out.println("Неизвестная команда, введите еще раз");
                        continue;
                    }
                    command.execute(argument);
                } catch (Exception e){
                    System.err.println("Ошибка: " + e.getMessage());
                }
            }
        }
    }