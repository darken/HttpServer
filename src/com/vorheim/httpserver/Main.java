package com.vorheim.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Main {

	public static int port = 8080;

	public static void main(String[] args) throws IOException {
		final var server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new HtmlHttpHandler());
		server.setExecutor(null);
		server.start();

		System.out.println("Running on port " + port);
	}
}
