package ru.practicum.exceptions;

public class EventDateIncorrectException extends RuntimeException {
    public EventDateIncorrectException(String message) {
        super(message);
    }
}
