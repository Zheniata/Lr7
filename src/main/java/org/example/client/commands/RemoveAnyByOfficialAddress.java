package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;

public class RemoveAnyByOfficialAddress extends Command{
    public RemoveAnyByOfficialAddress(ClientNetworkManager networkManager){
        super(networkManager);
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
            Response response = networkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла: " + e.getMessage());
        }
    }
}
