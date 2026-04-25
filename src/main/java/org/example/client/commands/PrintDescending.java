package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;

public class PrintDescending extends Command{
    ClientNetworkManager clientNetworkManager;
    public PrintDescending(ClientNetworkManager clientNetworkManager){
        this.clientNetworkManager = clientNetworkManager;
    }

    @Override
    public void execute(String argument) {
        try {
            Request request = new Request("print_descending", null, null);
            Response response = clientNetworkManager.sendRequest(request);
            System.out.println(response.getMessage());
            if (response.getData() != null){
                System.out.println(response.getData());
            }
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Произошла ошибка: " + e.getMessage());
        }

    }
}
