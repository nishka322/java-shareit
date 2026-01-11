
package ru.practicum.shareit.exceptions;

public class WrongRequestException extends RuntimeException {
    public WrongRequestException() {
    }

    public WrongRequestException(String message) {
        super(message);
    }
}