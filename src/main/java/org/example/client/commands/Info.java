package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;

public class Info extends Command{
    ClientNetworkManager networkManager;
    public Info(ClientNetworkManager networkManager){
        this.networkManager = networkManager;
    }

    @Override
    public void execute(String argument) {
        try{
            Request request = new Request("info", null, null);
            Response response = networkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
