package org.example.client;

import org.example.client.network.ClientNetworkManager;
import org.example.client.util.Runner;

import java.io.IOException;
import java.util.Scanner;

public class ClientApp {
    private static final String host = "localhost";
    private static final int port = 12345;

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Клиент запущен");
        System.out.println("Подключение к серверу " + host + ":" + port + "...");

        ClientNetworkManager network = new ClientNetworkManager(host, port);
        try {
            network.connect();
            System.out.println("Подключено");

            System.out.println("register - регестрация");
            System.out.println("login - вход");


            Runner runner = new Runner(scanner, network);
            runner.interactiveMode();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                network.disconnect();
                scanner.close();
                System.out.println("Соединение закрыто");
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }

    }
}
