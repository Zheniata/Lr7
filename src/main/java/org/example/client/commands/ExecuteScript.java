package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;
import org.example.common.models.Organization;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ExecuteScript extends Command{
    ClientNetworkManager clientNetworkManager;
    Scanner scanner;
    Set<Path> executingScripts = new HashSet<>();
    public ExecuteScript(ClientNetworkManager clientNetworkManager, Scanner scanner){
        this.clientNetworkManager = clientNetworkManager;
        this.scanner = scanner;
    }

    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.trim().isEmpty()) {
                System.out.println("Неверный формат команды, укажите путь к файлу");
                return;
            }

            String fileName = argument.trim();
            Path pathScript = Paths.get(fileName).toAbsolutePath().normalize();

            if (executingScripts.contains(pathScript)) {
                System.out.println("Ошибка. рекурсивное выполнение скрипта");
                return;
            }

            executeScriptFile(pathScript);

        } catch (Exception e){
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private void executeScriptFile(Path pathScript) throws IOException {
        if (!Files.exists(pathScript)) {
            System.out.println("Файл не найден: " + pathScript);
            return;
        }

        executingScripts.add(pathScript);

        try (BufferedReader reader = Files.newBufferedReader(pathScript)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmedLine = line.trim();

                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                    continue;
                }

                executeCommand(trimmedLine, lineNumber, pathScript);
            }
        } finally {
            executingScripts.remove(pathScript);
        }
    }

    private void executeCommand(String line, int lineNumber, Path scriptPath){
        try{
            if (line.startsWith("add") && line.contains("{") && line.endsWith("}")) {
                executeAddWithArgs(line, lineNumber);
                return;
            }

            String[] parts = line.split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            String commandArg = parts.length > 1 ? parts[1] : null;

            if (commandName.equals("execute_script")) {
                execute(commandArg);
                return;
            }

            if (commandName.equals("exit")) {
                System.out.println("Завершение работы...");
                System.exit(0);
                return;
            }

            Request request = new Request(commandName, commandArg, null);
            Response response = clientNetworkManager.sendRequest(request);

            if (!response.getMessage().isEmpty()) {
                System.out.println(response.getMessage());
            }

            if (response.getData() != null){
                System.out.println(response.getData());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeAddWithArgs(String line, int lineNumber) throws Exception{
        Organization org = Add.parseOrganization(line);

        Request request = new Request("add", null, org);
        Response response = clientNetworkManager.sendRequest(request);

        if (!response.getMessage().isEmpty()) {
            System.out.println(response.getMessage());
        }
    }
}
