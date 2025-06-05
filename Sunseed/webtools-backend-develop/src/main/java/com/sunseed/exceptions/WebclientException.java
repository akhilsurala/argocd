
package com.sunseed.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class WebclientException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private Object object = null;
    private HttpStatus httpStatus=HttpStatus.BAD_REQUEST;

    public WebclientException(String message) {
        super(message);

    }

    public WebclientException(Object object, String message) {
        super(message);
        this.object = object;
    }
    public WebclientException( String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus=httpStatus;
    }

}