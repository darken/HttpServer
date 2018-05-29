package com.vorheim.httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class IndexingHttpHandler implements HttpHandler {

	private static final String CONTENT_TYPE = "Content-Type";
	private static final String TEXT_HTML = "text/html";

	private File mainDir;

	public IndexingHttpHandler(File mainDir) {
		this.mainDir = mainDir;
	}

	private void doHandle(HttpExchange ex) throws IOException {
		var URI = ex.getRequestURI().toString();
		URI = URLDecoder.decode(URI, "UTF-8");
		var currentFile = new File(mainDir.getAbsolutePath() + File.separatorChar + URI);

		if (!currentFile.exists()) {
			sendNotFoundResponse(ex);
			return;
		}

		if (!currentFile.isDirectory()) {
			sendFileResponse(currentFile, ex);
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

		var html = HtmlBuilder.buildFileList(currentFile.getName(), URI, dirs, files);

		ex.getResponseHeaders().add(CONTENT_TYPE, TEXT_HTML);
		ex.sendResponseHeaders(200, html.length);

		var os = ex.getResponseBody();
		os.write(html, 0, html.length);
		os.close();
	}

	private void sendFileResponse(File file, HttpExchange ex) throws IOException {
		file = file.getCanonicalFile();

		var mime = Files.probeContentType(file.toPath());
		mime = mime == null ? "text/plain" : mime;

		ex.getResponseHeaders().set(CONTENT_TYPE, mime);
		ex.sendResponseHeaders(200, 0);

		var fis = new FileInputStream(file);
		var respBodyStream = ex.getResponseBody();
		var buffer = new byte[0x10000];

		int count = 0;
		while ((count = fis.read(buffer)) >= 0) {
			respBodyStream.write(buffer, 0, count);
		}
		fis.close();
		respBodyStream.close();
	}

	private void sendNotFoundResponse(HttpExchange ex) throws FileNotFoundException, IOException {
		var response = HtmlBuilder.readNotFound();

		ex.getResponseHeaders().add(CONTENT_TYPE, TEXT_HTML);
		ex.sendResponseHeaders(404, response.length);

		var os = ex.getResponseBody();
		os.write(response);
		os.close();
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
