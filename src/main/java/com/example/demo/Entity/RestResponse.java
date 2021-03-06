package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse {
    public static Integer ERROR_STORAGE = 411;

    private Integer error;
    private String message;
    private Object data;

    public RestResponse() {

    }

    public RestResponse(String message, Object data) {
        this.error = 0;
        this.message = message;
        this.data = data;
    }

    public RestResponse(Object data) {
        this.error = 0;
        this.message = "OK";
        this.data = data;
    }

    public RestResponse(String message, Integer error, Object data) {
        this.message = message;
        this.error = error;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }



    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }
}
