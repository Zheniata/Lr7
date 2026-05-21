package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;

public class Clear extends Command{
    ClientNetworkManager networkManager;
    public Clear(ClientNetworkManager networkManager){
        super(networkManager);
    }

    @Override
    public void execute(String argument) {
        try {
            Request request = new Request("clear", null, null);
            Response response = networkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }
}
