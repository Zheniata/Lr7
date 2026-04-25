package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;
import org.example.common.exceptions.MustBeNotEmptyException;
import org.example.common.exceptions.ValidationException;
import org.example.common.models.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

public class Add extends Command{
    private final ClientNetworkManager network;
    private final Scanner scanner;
    public Add(Scanner scanner, ClientNetworkManager network){
        this.network = network;
        this.scanner = scanner;
    }

    @Override
    public void execute(String argument) {
        try {
            Organization organization;

            if (argument != null && !argument.isEmpty() && argument.contains("{")) {
                organization = parseOrganization(argument);
            } else {
                organization = interactiveOrganization();
            }
            Request request = new Request("add", null, organization);
            Response response = network.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private Organization interactiveOrganization() throws Exception{
        String name;
        while (true){
            try {
                System.out.println("Введите имя:");
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    throw new MustBeNotEmptyException();
                }
                break;
            }
            catch (MustBeNotEmptyException e){
                System.out.println(e.getMessage());
            }
        }

        double x;
        while (true) {
            System.out.println("Введите координуту X:");
            String strX = scanner.nextLine().trim();
            try {
                x = Double.parseDouble(strX);
                if (x > 660) {
                    throw new ValidationException("x не может быть больше 660");
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат. Введите число");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }

        long y;
        while (true) {
            System.out.println("Введите координату Y:");
            String strY = scanner.nextLine().trim();
            try {
                y = Long.parseLong(strY);
                if (y <= -992) {
                    throw new ValidationException("y должен быть больше -992");
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат. Введите число");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }
        Coordinates coordinates = new Coordinates(x, y);


        float annualTurnover;
        while (true) {
            System.out.println("Введите значение годового оборота:");
            String strAnnualTurnover = scanner.nextLine().trim();
            try {
                annualTurnover = Float.parseFloat(strAnnualTurnover);
                if (annualTurnover <= 0) {
                    throw new ValidationException("Значение должно быть больше нуля");
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат. Введите число");
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
            }
        }

        OrganizationType type;
        while (true) {
            System.out.print("Введите тип [TRUST, PRIVATE_LIMITED_COMPANY, OPEN_JOINT_STOCK_COMPANY]: ");
            String typeStr = scanner.nextLine().trim().toUpperCase();
            try {
                type = OrganizationType.valueOf(typeStr);
                break;
            }
            catch (IllegalArgumentException e) {
                System.out.println("Такого type не сущетсвует, введите еще раз");
            }
        }

        System.out.println("Введите улицу:");
        String street = scanner.nextLine().trim();
        if (street.isEmpty()){
            street = null;
        }
        System.out.println("Введите индекс:");
        String zipCode = scanner.nextLine().trim();
        if (zipCode.isEmpty()){
            zipCode = null;
        }
        Address address;
        if (street != null || zipCode != null){
            address = new Address(street, zipCode);
        } else {
            address = null;
        }

        Organization organization = new Organization(name, coordinates, annualTurnover, type, address);
        return organization;
    }

    public static Organization parseOrganization(String argument) throws Exception{
        int startBrace = argument.indexOf('{');
        int endBrace = argument.lastIndexOf('}');

        if (startBrace == -1 || endBrace == -1 || endBrace <= startBrace) {
            throw new Exception("Неверный формат скобок");
        }
        String content = argument.substring(startBrace + 1, endBrace).trim();

        String[] pairs = content.split(",(?=([^\']*\'[^\']*\')*[^\']*$)");

        String name = null;
        Double x = null;
        Long y = null;
        Float annualTurnover = null;
        OrganizationType type = null;
        String street = null;
        String zipCode = null;

        for (String pair : pairs) {
            pair = pair.trim();
            if (!pair.contains("=")) {
                throw new Exception("Некорректная пара: " + pair);
            }

            String[] kv = pair.split("=", 2);
            if (kv.length < 2) {
                throw new Exception("Не хватает значения в паре: " + pair);
            }

            String key = kv[0].trim();
            String value = kv[1].trim();

            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }

            try {
                switch (key) {
                    case "name" -> name = value;
                    case "x" -> x = Double.parseDouble(value);
                    case "y" -> y = Long.parseLong(value);
                    case "annualTurnover" -> annualTurnover = Float.parseFloat(value);
                    case "type" -> type = OrganizationType.valueOf(value.toUpperCase());
                    case "street" -> street = value;
                    case "zipCode" -> zipCode = value;
                    default -> throw new Exception("Неизвестное поле: '" + key + "'");
                }
            } catch (Exception e) {
                throw new Exception("Ошибка в поле '" + key + "': " + e.getMessage());
            }
        }
        if (name == null || name.isEmpty()) {
            throw new Exception("Имя обязательно");
        }
        if (x == null || y == null) {
            throw new Exception("Координаты x и y обязательны");
        }
        if (annualTurnover == null || annualTurnover <= 0) {
            throw new Exception("annualTurnover должен быть > 0");
        }
        if (type == null) {
            throw new Exception("Тип обязателен");
        }

        Coordinates coordinates = new Coordinates(x, y);
        Address address = (street != null || zipCode != null) ? new Address(street, zipCode) : null;

        return new Organization(name, coordinates, annualTurnover, type, address);
    }
}
