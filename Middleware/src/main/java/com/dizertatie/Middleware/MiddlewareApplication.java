package com.dizertatie.Middleware;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.xnio.IoUtils;
import org.xnio.OptionMap;

import io.undertow.Undertow;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;
import io.undertow.util.Headers;
import io.undertow.Handlers;


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

class ReverseProxyClient implements ProxyClient {
    private static final ProxyTarget TARGET = new ProxyTarget() {};

    private final UndertowClient client;
    private final URI servletUri, reactiveUri;
    private boolean pingpong = true;

    public ReverseProxyClient(URI servletUri, URI reactiveUri) {
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

@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
public class MiddlewareApplication {

    @SneakyThrows
    public static void main(String[] args) {
        var tomcatBuilder = makeBuilder(8080, MiddlewareApplication.class)
                .properties("middleware.type=SERVLET")
                .web(WebApplicationType.SERVLET);

        var nettyBuilder = makeBuilder(8081, MiddlewareApplication.class)
                .properties("middleware.type=REACTIVE")
                .web(WebApplicationType.REACTIVE);

        tomcatBuilder.run(args);
        nettyBuilder.run(args);

        ReverseProxyClient pingPongClient = new ReverseProxyClient(
                URI.create("http://127.0.0.1:8080"),
                URI.create("http://127.0.0.1:8081"));

        Undertow reverseProxy = Undertow.builder()
                .addHttpListener(8083, "localhost")
                .setHandler(Handlers.proxyHandler(pingPongClient))
                .build();
        reverseProxy.start();
    }

    private static SpringApplicationBuilder makeBuilder(Integer port, Class<?>... sources) {
        return new SpringApplicationBuilder(MiddlewareApplication.class)
                .child(sources)
                .properties(String.format("server.port=%d", port));
    }
}
