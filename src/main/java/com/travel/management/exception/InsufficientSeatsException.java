package com.travel.management.exception;

public class InsufficientSeatsException extends RuntimeException {
    public InsufficientSeatsException(String message) {
        super(message);
    }

    public InsufficientSeatsException(int requested, int available) {
        super(String.format("Requested %d seats, but only %d seats are available.", requested, available));
    }
}
