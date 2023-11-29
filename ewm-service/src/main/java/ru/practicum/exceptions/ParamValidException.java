package ru.practicum.exceptions;

public class ParamValidException extends RuntimeException {
    public ParamValidException(String message) {
        super(message);
    }
}
