package by.danilov.cs.lab2.exception;

public class BadRequestException extends Exception {

    public BadRequestException() {
        super("400 Bad Request");
    }

    public BadRequestException(Exception e) {
        super(e.getMessage());
    }
}
