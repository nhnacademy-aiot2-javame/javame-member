package com.nhnacademy.common.exception;

import com.nhnacademy.common.dto.ErrorResponse;
import com.nhnacademy.company.common.AlreadyExistCompanyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 핸들러 클래스입니다.
 * @RestControllerAdvice 를 통해 모든 @RestController 에서 발생하는 예외를 감지하고 처리합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 이미 존재하는 회사 정보 생성을 시도할 때 발생하는 {@link AlreadyExistCompanyException}을 처리합니다.
     *
     * @param ex 발생한 AlreadyExistCompanyException 객체
     * @return 409 상태 코드와 에러 정보를 담은 ResponseEntity
     */
    /**
     * 리소스가 이미 존재할 때 발생하는 예외 (하위 예외 포함: AlreadyExistCompanyException 등)를 처리합니다.
     * HTTP 상태 코드 409 (Conflict)를 반환합니다.
     *
     * @param ex 발생한 ResourceAlreadyExistsException 또는 그 하위 예외
     * @return 409 상태 코드와 에러 정보를 담은 ResponseEntity
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        log.warn("Conflict Exception: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * 요청한 리소스를 찾을 수 없을 때 발생하는 예외 (하위 예외 포함: NotExistCompanyException 등)를 처리합니다.
     * HTTP 상태 코드 404 (Not Found)를 반환합니다.
     *
     * @param ex 발생한 ResourceNotFoundException 또는 그 하위 예외
     * @return 404 상태 코드와 에러 정보를 담은 ResponseEntity
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Not Found Exception: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * {@code @Valid} 어노테이션이 적용된 요청 본문(DTO)의 유효성 검증 실패 시 발생하는
     * {@link MethodArgumentNotValidException}을 처리합니다.
     * 발생한 모든 필드 검증 오류 메시지를 취합하여 반환합니다.
     * HTTP 상태 코드 400 (Bad Request)와 함께 에러 응답을 반환합니다.
     *
     * @param ex 발생한 MethodArgumentNotValidException 객체
     * @return 400 상태 코드와 검증 오류 상세 정보를 포함한 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("[%s] %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        log.warn("Validation Exception: {}", errorMessage);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 위에서 명시적으로 처리되지 않은 모든 종류의 {@link Exception}을 처리하는 최종 핸들러입니다.
     * 예상치 못한 서버 내부 오류 상황에 해당하며, 상세한 스택 트레이스를 로그로 기록합니다.
     * 클라이언트에게는 민감한 정보 노출을 피하기 위해 일반적인 오류 메시지만 반환합니다.
     * HTTP 상태 코드 500 (Internal Server Error)와 함께 에러 응답을 반환합니다.
     *
     * @param ex 발생한 Exception 객체
     * @return 500 상태 코드와 일반적인 에러 메시지를 담은 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unhandled Exception occurred: ", ex);
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "처리 중 예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Input",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request Body",
                "요청 본문이 잘못되었습니다. JSON 형식이 올바른지 확인해주세요."
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

}
