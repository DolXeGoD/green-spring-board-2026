package com.green.spring_board.exceptions;

// 요청한 작업을 수행하기에 현재 객체의 상태가 올바르지 않다.
// 현재 상태에서는 해당 작업을 수행할 수 없음
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
