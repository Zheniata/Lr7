package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import javax.imageio.IIOException;
import java.io.IOException;

public class History extends Command{

    public History(ClientNetworkManager networkManager){
        super(networkManager);
    }

    @Override
    public void execute(String argument) {
        try{
            Request request = new Request("history", null, null);
            Response response = networkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }
}
