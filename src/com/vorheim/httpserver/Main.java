package com.vorheim.httpserver;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class Main {

	private int port = 8080;
	private File mainDir;

	public static void main(String[] args) {
		try {
			new Main().start(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void start(String[] args) throws IOException {
		if (!processArguments(args)) {
			System.out.println("Usage: http-server [path] [options]");
			System.out.println("\t[path] \t\tPath to main directory, defaults to \"./\".");
			System.out.println("\t-p xxxx\t\tPort to use, defaults to 8080.");
			return;
		}

		final var server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new IndexingHttpHandler(mainDir));
		server.setExecutor(null);
		server.start();

		System.out.println("Running on http://localhost:" + port);
	}

	private boolean processArguments(String[] args) {
		mainDir = new File(System.getProperty("user.dir"));

		if (args.length < 1) {
			return true;
		}

		var index = 0;
		if (!args[0].startsWith("-")) {
			if (!setMainDir(args[0])) {
				return false;
			}
			index++;
		}

		for (; index < args.length; index++) {
			var option = args[index];
			String value;

			switch (option) {
			case "-p":
				if (index + 1 >= args.length) {
					System.out.println("Port number needed [-p 1234].");
					return false;
				}

				value = args[++index];
				if (!setPort(value))
					return false;
				break;
			default:
				System.out.println(option + " is not a valid option.");
				return false;
			}
		}

		return true;
	}

	private boolean setPort(String portValue) {
		try {
			port = Integer.parseInt(portValue);
		} catch (NumberFormatException e) {
			System.out.println(portValue + " is not a valid port.");
			return false;
		}
		return true;
	}

	private boolean setMainDir(String path) {
		mainDir = new File(path);
		if (mainDir.isDirectory()) {
			return true;
		}

		System.out.println("Path not valid.");
		return false;
	}
}
