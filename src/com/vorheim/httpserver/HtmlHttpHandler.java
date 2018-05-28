package com.vorheim.httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HtmlHttpHandler implements HttpHandler {

	private FileScanner scanner = new FileScanner();

	private void doHandle(HttpExchange ex) throws IOException {
		var URI = ex.getRequestURI().toString();
		URI = URLDecoder.decode(URI, "UTF-8");
		var currentFile = scanner.getCurrentFile(URI);

		if (!currentFile.isDirectory()) {
			sendFile(currentFile, ex);
			return;
		}

		var dirs = new ArrayList<File>();
		var files = new ArrayList<File>();
		for (File file : currentFile.listFiles()) {
			if (file.isDirectory())
				dirs.add(file);
			else
				files.add(file);
		}

		var template = readTemplate("list.html");
		var html = HtmlBuilder.build(template, currentFile.getName(), URI, dirs, files);

		ex.getResponseHeaders().add("Content-Type", "text/html");
		ex.sendResponseHeaders(200, html.length);

		var os = ex.getResponseBody();
		os.write(html, 0, html.length);
		os.close();
	}

	public void sendFile(File file, HttpExchange ex) throws IOException {
		file = file.getCanonicalFile();

		if (!file.isFile()) {
			sendNotFoundResponse(ex);
		} else {
			sendFileResponse(file, ex);
		}
	}

	private void sendFileResponse(File file, HttpExchange ex) throws IOException, FileNotFoundException {
		var mime = Files.probeContentType(file.toPath());
		mime = mime == null ? "text/plain" : mime;

		ex.getResponseHeaders().set("Content-Type", mime);
		ex.sendResponseHeaders(200, 0);

		OutputStream os = ex.getResponseBody();
		FileInputStream fs = new FileInputStream(file);
		final byte[] buffer = new byte[0x10000];
		int count = 0;
		while ((count = fs.read(buffer)) >= 0) {
			os.write(buffer, 0, count);
		}
		fs.close();
		os.close();
	}

	private void sendNotFoundResponse(HttpExchange ex) throws FileNotFoundException, IOException {
		var response = readTemplate("notfound.html");
		ex.getResponseHeaders().add("Content-Type", "text/html");
		ex.sendResponseHeaders(404, response.length);

		var os = ex.getResponseBody();
		os.write(response);
		os.close();
	}

	private byte[] readTemplate(String fileName) throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream("com/vorheim/httpserver/" + fileName);
		return is.readAllBytes();
	}

	@Override
	public void handle(HttpExchange ex) throws IOException {
		try {
			doHandle(ex);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

}
