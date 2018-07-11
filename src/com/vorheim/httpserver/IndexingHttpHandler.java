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
	private static final String TEXT_PLAIN = "text/plain";

	private File mainDir;

	public IndexingHttpHandler(File mainDir) {
		this.mainDir = mainDir;
	}

	private void doHandle(HttpExchange ex) throws IOException {
		var url = URLDecoder.decode(ex.getRequestURI().toString(), "UTF-8");
		var currentFile = new File(mainDir.getAbsolutePath() + File.separatorChar + url);

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
			var list = file.isDirectory() ? dirs : files;
			list.add(file);
		}

		var html = HtmlBuilder.buildFileList(currentFile.getName(), url, dirs, files);

		ex.getResponseHeaders().add(CONTENT_TYPE, TEXT_HTML);
		ex.sendResponseHeaders(200, html.length);

		var os = ex.getResponseBody();
		os.write(html, 0, html.length);
		os.close();
	}

	private void sendFileResponse(File file, HttpExchange ex) throws IOException {
		file = file.getCanonicalFile();

		ex.getResponseHeaders().set(CONTENT_TYPE, getMime(file));
		ex.sendResponseHeaders(200, file.length());

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

	private static String getMime(File file) throws IOException {
		var mime = Files.probeContentType(file.toPath());
		return mime != null ? mime : TEXT_PLAIN;
	}
}
