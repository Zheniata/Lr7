package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;

public class Clear extends Command{
    ClientNetworkManager clientNetworkManager;
    public Clear(ClientNetworkManager clientNetworkManager){
        this.clientNetworkManager = clientNetworkManager;
    }

    @Override
    public void execute(String argument) {
        try {
            Request request = new Request("clear", null, null);
            Response response = clientNetworkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }
}
