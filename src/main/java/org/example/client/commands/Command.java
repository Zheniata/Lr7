package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;

import java.util.Scanner;

public abstract class Command {
    public ClientNetworkManager networkManager;
    public String login;
    public String password;

    public Command(ClientNetworkManager networkManager){
        this.networkManager = networkManager;
    }

    public Command() {
        this.networkManager = null;
        this.login = null;
        this.password = null;
    }

    public void execute(String argument){}
}
