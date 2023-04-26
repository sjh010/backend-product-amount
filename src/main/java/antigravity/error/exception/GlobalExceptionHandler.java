package antigravity.error.exception;

import antigravity.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse handleMethodArgumentNotValidException(BindException e) {

        List<String> errors = e.getBindingResult().getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());

        log.error("Validation Exception : {}", errors);

        return ErrorResponse.builder()
                .code(400)
                .message(errors.toString())
                .build();
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.OK)
    protected ErrorResponse customException(CustomException e) {
        log.error(e.getErrorCode().getMessage(), e);
        return new ErrorResponse(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorResponse defaultException(Exception e) {
        log.error(e.getMessage(), e);
        return ErrorResponse.builder()
                .code(500)
                .message("서버 오류")
                .build();
    }


}
