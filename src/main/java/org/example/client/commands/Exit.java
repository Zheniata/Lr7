package org.example.client.commands;

public class Exit extends Command{

    public Exit() {
        super();
    }

    @Override
    public void execute(String argument) {
        System.out.println("Работа завершена");
        System.exit(0);
    }
}
