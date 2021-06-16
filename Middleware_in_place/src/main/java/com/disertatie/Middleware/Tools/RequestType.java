package com.disertatie.Middleware.Tools;

public enum RequestType {
    NONE,
    BLOCKING,
    NONBLOCKING;

    private RequestType opposite;
    static {
        NONE.opposite = NONE;
        BLOCKING.opposite = NONBLOCKING;
        NONBLOCKING.opposite = BLOCKING;
    }

    public RequestType oppositeType() {
        return opposite;
    }
}
