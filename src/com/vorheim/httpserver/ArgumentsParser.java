package com.vorheim.httpserver;

import java.io.File;
import java.util.Optional;

public class ArgumentsParser {

	private int port = 8080;
	private File mainDir = new File(System.getProperty("user.dir"));

	private Optional<String> errMsg = Optional.empty();

	public ArgumentsParser(String[] args) {
		processArguments(args);
		if (errMsg.isPresent()) {
			System.out.println(errMsg.get());

			System.out.println("Usage: http-server [path] [options]");
			System.out.println("\t[path] \t\tPath to main directory, defaults to \"./\".");
			System.out.println("\t-p xxxx\t\tPort to use, defaults to 8080.");
		}
	}

	public boolean hasErrors() {
		return errMsg.isPresent();
	}

	private void processArguments(String[] args) {
		if (args.length < 1)
			return;

		var index = 0;
		// Validate first argument and try to set it as main directory
		if (!args[0].startsWith("-")) {
			index++;

			if (!setMainDir(args[0]))
				return;
		}

		for (; index < args.length; index++) {
			var option = args[index];

			switch (option) {
			case "-p":
				// Get next argument and set it as the port
				if (++index >= args.length) {
					setErr("Port number required [-p 1234].");
					return;
				}

				if (!setPort(args[index]))
					return;

				break;
			default:
				setErr("Port number required [-p 1234].");
				return;
			}
		}
	}

	private void setErr(String msg) {
		errMsg = Optional.of(msg);
	}

	private boolean setMainDir(String path) {
		mainDir = new File(path);

		if (!mainDir.isDirectory()) {
			setErr("Path not valid.");
			return false;
		}
		return true;
	}

	private boolean setPort(String portValue) {
		try {
			port = Integer.parseInt(portValue);
		} catch (NumberFormatException e) {
			setErr(portValue + " is not a valid port.");
			return false;
		}
		return true;
	}

	public int getPort() {
		return port;
	}

	public File getMainDir() {
		return mainDir;
	}

}
