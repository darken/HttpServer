package com.vorheim.httpserver;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HtmlBuilder {

	private static final String URL_SEPARATOR = "/";
	private static final String TEMPLATE_DIR = "com/vorheim/httpserver/";

	private static byte[] notFound = null;

	public static byte[] buildFileList(String currentDirName, String relativePath, List<File> dirs, List<File> files)
			throws IOException {
		var html = new String(readTemplate("list.html"), "UTF-8");
		var sb = new StringBuilder();

		if (!URL_SEPARATOR.equals(relativePath)) {
			// When relative path a child directory
			sb.append(makeLink("/..", "..", true));
		}

		appendList(sb, relativePath, dirs, true);
		appendList(sb, relativePath, files, false);

		html = html.replace("{{currentDir}}", currentDirName);
		html = html.replace("{{list}}", sb.toString());
		return html.getBytes();
	}

	public static byte[] readNotFound() throws IOException {
		return notFound != null ? notFound : (notFound = readTemplate("notfound.html"));
	}

	private static byte[] readTemplate(String fileName) throws IOException {
		var is = HtmlBuilder.class.getClassLoader().getResourceAsStream(TEMPLATE_DIR + fileName);
		return is.readAllBytes();
	}

	private static void appendList(StringBuilder sb, String relativePath, List<File> files, boolean areDirs) {
		files.stream().forEach(file -> {
			var path = !URL_SEPARATOR.equals(relativePath) ? relativePath + file.getName() : file.getName();
			String name;

			if (areDirs) {
				name = URL_SEPARATOR + file.getName();
				path += URL_SEPARATOR;
			} else {
				name = file.getName();
			}

			sb.append(makeLink(name, path, areDirs));
		});
	}

	private static String makeLink(String name, String path, boolean isDir) {
		var sb = new StringBuilder();

		sb.append("<a href='");
		sb.append(path);
		sb.append("'");
		if (!isDir) {
			sb.append(" target='_blank'");
		}
		sb.append(">");
		sb.append(name);
		sb.append("</a><br>");

		return sb.toString();
	}
}
