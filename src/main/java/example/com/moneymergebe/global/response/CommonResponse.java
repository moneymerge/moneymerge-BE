package example.com.moneymergebe.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {
    private final Integer statusCode;
    private final String message;

    @JsonInclude(Include.NON_EMPTY)
    private T data;

    public CommonResponse(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

}
