package com.qr_restaurant.common.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * Turns the exceptions the domain and Spring Data throw into REST responses for every controller.
 * Views don't go through this; they catch the same exceptions themselves.
 */
@RestControllerAdvice
class ApiExceptionHandler {

    // Something the request refers to doesn't exist, e.g. an unknown order or table id.
    @ExceptionHandler(NoSuchElementException.class)
    ProblemDetail handleNotFound(NoSuchElementException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // The request itself is wrong, e.g. an order line for a menu item that doesn't exist.
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidRequest(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // The domain refused the change in the current state, e.g. an invalid status transition,
    // a table that is already taken, or a catalogue that still has menu items.
    @ExceptionHandler(IllegalStateException.class)
    ProblemDetail handleInvalidState(IllegalStateException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    // Another request committed a change to the same row between our read and our write.
    @ExceptionHandler(OptimisticLockingFailureException.class)
    ProblemDetail handleOptimisticLock(OptimisticLockingFailureException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "The resource was modified by another request. Reload it and try again.");
    }

    // A concurrent request won a race the checks in the commands can't see, e.g. two creates with
    // the same id, or a menu item added to a catalogue while it was being deleted.
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleIntegrityViolation(DataIntegrityViolationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "The change conflicts with another request. Reload and try again.");
    }
}
