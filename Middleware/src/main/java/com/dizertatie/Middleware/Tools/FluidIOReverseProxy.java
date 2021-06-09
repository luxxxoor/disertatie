package com.dizertatie.Middleware.Tools;

import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;
import org.xnio.IoUtils;
import org.xnio.OptionMap;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;


/**
 * Start the ReverseProxy with an ImmutableMap of matching endpoints and a default
 *
 * Example:
 * mapping: ImmutableMap("api" -> "http://some-domain.com")
 * default: "http://default-domain.com"
 *
 * Request 1: localhost:8080/foo -> http://default-domain.com/foo
 * Request 2: localhost:8080/api/bar -> http://some-domain.com/bar
 */
public class FluidIOReverseProxy implements ProxyClient {
    private static final ProxyTarget TARGET = new ProxyTarget() {};

    private final UndertowClient client;
    private final URI servletUri, reactiveUri;
    private boolean pingpong = true;

    public FluidIOReverseProxy(URI servletUri, URI reactiveUri) {
        this.client = UndertowClient.getInstance();
        this.servletUri = servletUri;
        this.reactiveUri = reactiveUri;
    }

    @Override
    public ProxyTarget findTarget(HttpServerExchange exchange) {
        return TARGET;
    }

    @Override
    public void getConnection(ProxyTarget target, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
        URI targetUri = pingpong ? servletUri : reactiveUri;
        pingpong = !pingpong;

        client.connect(
                new ConnectNotifier(callback, exchange),
                targetUri,
                exchange.getIoThread(),
                exchange.getConnection().getByteBufferPool(),
                OptionMap.EMPTY);
    }

    private final class ConnectNotifier implements ClientCallback<ClientConnection> {
        private final ProxyCallback<ProxyConnection> callback;
        private final HttpServerExchange exchange;

        private ConnectNotifier(ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange) {
            this.callback = callback;
            this.exchange = exchange;
        }

        @Override
        public void completed(final ClientConnection connection) {
            final ServerConnection serverConnection = exchange.getConnection();
            serverConnection.addCloseListener(serverConnection1 -> IoUtils.safeClose(connection));
            callback.completed(exchange, new ProxyConnection(connection, "/"));
        }

        @Override
        public void failed(IOException e) {
            callback.failed(exchange);
        }
    }
}