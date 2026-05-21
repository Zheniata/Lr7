package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

import java.io.IOException;

public class PrintFieldDescendingType extends Command{
    public PrintFieldDescendingType(ClientNetworkManager networkManager){
        super(networkManager);
    }

    @Override
    public void execute(String argument) {
        try{
            Request request = new Request("print_field_descending_type", null, null);
            Response response = networkManager.sendRequest(request);
            System.out.println(response.getMessage());
            if (response.getData() != null){
                System.out.println(response.getData());
            }
        }catch (IOException | ClassNotFoundException e){
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }
}
