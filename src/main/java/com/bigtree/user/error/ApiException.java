package com.bigtree.user.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException{

    HttpStatus status;
    String detail;
    String statusCode;
    String reasonPhrase;

    public ApiException(HttpStatus status, String message){
        super (message);
        this.status = status;
        this.reasonPhrase  = status.getReasonPhrase();
        this.statusCode  = status.toString();
    }

}
