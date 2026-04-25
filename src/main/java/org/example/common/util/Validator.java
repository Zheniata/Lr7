package org.example.common.util;

import org.example.common.models.*;

public class Validator {
    public static void validateId(long id){
        if (id <= 0){
            throw new IllegalArgumentException("ID должно быть большее нуля");
        }
    }

    /**
     * Валидирует название организации.
     *
     * @param name проверяемое название
     * @throws IllegalArgumentException если название {@code null} или пустое
     */

    public static void validateName(String name){
        if (name == null || name.isEmpty()){
            throw new IllegalArgumentException("Имя пользователя не должно быть путсым или null");
        }
    }

    /**
     * Валидирует годовой оборот.
     *
     * @param turnover проверяемое значение
     * @throws IllegalArgumentException если оборот ≤ 0
     */

    public static void validateAnnualTurnover(double turnover) {
        if (turnover <= 0) {
            throw new IllegalArgumentException("Годовой оборот должен быть больше 0");
        }
    }

    /**
     * Валидирует тип организации.
     *
     * @param type проверяемый тип
     * @throws IllegalArgumentException если тип {@code null}
     */

    public static void validateType(OrganizationType type) {
        if (type == null) {
            throw new IllegalArgumentException("Тип организации не может быть null");
        }
    }

    /**
     * Валидирует координату X.
     *
     * @param x проверяемое значение
     * @throws IllegalArgumentException если X > 606
     */

    public static void validateX(double x){
        if (x > 606){
            throw new IllegalArgumentException("X не должен быть больше 606");
        }
    }

    /**
     * Валидирует координату Y.
     *
     * @param y проверяемое значение
     * @throws IllegalArgumentException если Y ≤ -992
     */

    public static void validateY(long y){
        if (y <= -992){
            throw new IllegalArgumentException("Y должен быть больше -992");
        }
    }

    /**
     * Валидирует объект координат целиком.
     *
     * @param coordinates проверяемый объект
     * @throws IllegalArgumentException если координаты {@code null} или содержат недопустимые значения
     */

    public static void validateCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Координаты не могут быть null");
        }
        Validator.validateX(coordinates.getX());
        Validator.validateY(coordinates.getY());
    }
}
