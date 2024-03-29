package io.hexlet.exercise;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

public class Main {
    //Base URI the Grizzly HTTP server will listen
    public static final String BASE_URI = "http://localhost:8080/api";

    /**
     * Старт Grizzly HTTP Server на JAX-RS
     * @return Grizzly HTTP Server
     */
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig().packages("io.hexlet.exercise.res");

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.printf("Jersey app started with WADL available at "
                + "%s application.wadl\nHit enter to stop it...%n", BASE_URI);
        System.in.read();
        server.shutdownNow();
    }

}