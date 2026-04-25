package org.example.common.exceptions;

public class MustBeNotEmptyException extends RuntimeException{
    public MustBeNotEmptyException() {
        super("Поле не может быть пустым");
    }
}
