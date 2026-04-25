package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

public class RemoveAnyByOfficialAddress extends Command{
    ClientNetworkManager clientNetworkManager;
    public RemoveAnyByOfficialAddress(ClientNetworkManager clientNetworkManager){
        this.clientNetworkManager = clientNetworkManager;
    }

    @Override
    public void execute(String argument) {
        try{
            if (argument == null || argument.trim().isEmpty()) {
                System.out.println("Неверный формат команды, введите id");
                return;
            }

            String street = argument.trim();
            Request request = new Request("remove_any_by_official_address", street, null);
            Response response = clientNetworkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла: " + e.getMessage());
        }
    }
}
