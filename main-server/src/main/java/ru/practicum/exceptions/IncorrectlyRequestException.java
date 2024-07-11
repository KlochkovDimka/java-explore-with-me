package ru.practicum.exceptions;

public class IncorrectlyRequestException extends RuntimeException {
    public IncorrectlyRequestException(String message) {
        super(message);
    }
}
