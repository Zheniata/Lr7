package org.example.server.commands;

import org.example.common.*;

public abstract class Command {
    private final String name;

    public Command(String name){
        this.name = name;
    }
    public abstract Response execute(Request request, User user);

    public String getName() {
        return name;
    }
}
