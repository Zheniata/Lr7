package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;
import java.util.Scanner;

public class Login extends Command{
    private Scanner scanner;

    public Login(ClientNetworkManager clientNetworkManager, Scanner scanner){
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
                System.out.println("Логин и пароль не могут быть пустыми");
                return;
            }

            Request request = new Request("login", login + " " + password, null);
            Response response = networkManager.sendRequest(request);

            System.out.println(response.getMessage());

            if (response.isSuccess()) {
                System.out.println("Добро пожаловать, " + login);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
