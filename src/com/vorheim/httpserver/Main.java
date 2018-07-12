package com.vorheim.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Main {

	public static void main(String[] args) {
		try {
			var parser = new ArgumentsParser(args);
			new Main().start(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(ArgumentsParser parser) throws IOException {
		if (parser.hasErrors()) {
			return;
		}

		final var server = HttpServer.create(new InetSocketAddress(parser.getPort()), 0);
		server.createContext("/", new IndexingHttpHandler(parser.getMainDir()));
		server.setExecutor(null);
		server.start();

		System.out.println("Running on http://localhost:" + parser.getPort());
	}

}
