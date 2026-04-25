package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;

public class Help extends Command{
    ClientNetworkManager clientNetworkManager;

    public Help(ClientNetworkManager clientNetworkManager){
        this.clientNetworkManager = clientNetworkManager;
    }
    @Override
    public void execute(String argument) {
        try{
            Request request = new Request("help", null, null);
            Response response = clientNetworkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Произошла ошибка" + e.getMessage());
        }
    }
}
