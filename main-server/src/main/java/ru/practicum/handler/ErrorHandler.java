package ru.practicum.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.practicum.exceptions.IncorrectlyRequestException;
import ru.practicum.exceptions.ViolationOfRestrictionException;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.model.ApiError;
import ru.practicum.model.enams.StateError;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ViolationOfRestrictionException.class,
            DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflict(RuntimeException e) {
        return ApiError.builder()
                .status(StateError._409_CONFLICT.toString())
                .reason("Integrity constraint has been violated")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @ExceptionHandler({NotFoundEntity.class,
            HttpClientErrorException.BadRequest.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(RuntimeException e) {
        return ApiError.builder()
                .status(StateError._404_NOT_FOUND.toString())
                .reason("The required object was not found.")
                .message(String.format("%s was not found", e))
                .build();
    }

    @ExceptionHandler({IncorrectlyRequestException.class,
            IllegalStateException.class,
            IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequest(RuntimeException e) {
        return ApiError.builder()
                .status(StateError._400_BAD_REQUEST.name())
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .build();
    }
}
