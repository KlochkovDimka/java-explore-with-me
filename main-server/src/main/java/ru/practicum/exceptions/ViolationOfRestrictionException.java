package ru.practicum.exceptions;

public class ViolationOfRestrictionException extends RuntimeException {

    public ViolationOfRestrictionException(String message) {
        super(message);
    }
}
