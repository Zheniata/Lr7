package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;

public class RemoveGreater extends Command{
    private ClientNetworkManager clientNetworkManager;
    public RemoveGreater(ClientNetworkManager clientNetworkManager){
        this.clientNetworkManager = clientNetworkManager;
    }

    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.trim().isEmpty()) {
                System.out.println("Неверный формат команды, введите id");
                return;
            }
            String idArg = argument.trim();
            Request request = new Request("remove_greater", idArg, null);
            Response response = clientNetworkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }
}
