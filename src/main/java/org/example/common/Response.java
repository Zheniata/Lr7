package org.example.common;

import java.io.Serial;
import java.io.Serializable;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String message;
    private Object data;
    private User user;

    public Response(boolean success, String message, Object data, User user) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.user = user;
    }

    public Response(boolean success, String message, Object data) {
        this(success, message, data, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public User getUser() {
        return user;
    }
}
