package org.example.client.commands;

import org.example.client.network.ClientNetworkManager;
import org.example.common.Request;
import org.example.common.Response;
import org.example.common.exceptions.MustBeNotEmptyException;
import org.example.common.exceptions.ValidationException;
import org.example.common.models.Address;
import org.example.common.models.Organization;
import org.example.common.models.OrganizationType;
import org.example.common.models.Coordinates;


import java.util.Scanner;

public class Update extends Command{
    ClientNetworkManager clientNetworkManager;
    Scanner scanner;
    public Update(Scanner scanner, ClientNetworkManager clientNetworkManager){
        this.clientNetworkManager = clientNetworkManager;
        this.scanner = scanner;
    }

    @Override
    public void execute(String argument) {
        try {
            if (argument == null || argument.trim().isEmpty()) {
                System.out.println("Неверный формат команды, введите id");
                return;
            }
            String idArg = argument.trim().toLowerCase()
                    .replaceAll("^id\\s+", "")
                    .trim();;

            Organization updatedOrg = updatedOrganization(idArg);

            Request request = new Request("update", idArg,  updatedOrg);
            Response response = clientNetworkManager.sendRequest(request);
            System.out.println(response.getMessage());
        } catch (Exception e){
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }

    private Organization updatedOrganization(String idArg) throws Exception{
        System.out.println("Введите новые данные для организации");

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
        return new Organization(name, coordinates, annualTurnover, type, address);
    }
}
