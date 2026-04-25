package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import javax.imageio.IIOException;
import java.io.IOException;

public class Show extends Command{
    ClientNetworkManager networkManager;
    public Show(ClientNetworkManager networkManager){
        this.networkManager = networkManager;
    }

    @Override
    public void execute(String argument) {
        try{
            Request request = new Request("show", null, null);
            Response response = networkManager.sendRequest(request);
            System.out.println(response.getMessage());
            if (response.getData() != null) {
                System.out.println(response.getData());
            }
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Ошибка: " + e.getMessage());
        }

    }
}
