package example.com.moneymergebe.global.exception;

import example.com.moneymergebe.global.response.ResultCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GlobalException extends RuntimeException{
    private final ResultCode resultCode;
}
