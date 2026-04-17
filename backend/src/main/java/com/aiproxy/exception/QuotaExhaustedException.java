package com.aiproxy.exception;

import java.time.LocalDate;

public class QuotaExhaustedException extends RuntimeException {

    private final LocalDate resetDate;

    public QuotaExhaustedException(String message, LocalDate resetDate) {
        super(message);
        this.resetDate = resetDate;
    }

    public LocalDate getResetDate() {
        return resetDate;
    }
}
