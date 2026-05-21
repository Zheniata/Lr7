package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;
import java.util.Scanner;

public class Register extends Command{
    private final Scanner scanner;

    public Register(ClientNetworkManager clientNetworkManager, Scanner scanner){
        super(clientNetworkManager);
        this.scanner = scanner;
    }

    @Override
    public void execute(String argument) {
        try {
            System.out.print("Введите логин: ");
            String login = scanner.nextLine().trim();

            System.out.print("Введите пароль: ");
            String password = scanner.nextLine().trim();

            if (login.isEmpty() || password.isEmpty()) {
                System.err.println("Логин и пароль не могут быть пустыми");
                return;
            }

            Request request = new Request("register", login + " " + password, null);
            Response response = networkManager.sendRequest(request);

            System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка связи с сервером: " + e.getMessage());
        }
    }
}
