package com.vorheim.httpserver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

public class HtmlBuilder {

	public static byte[] build(byte[] template, String currentDirName, String relativePath, List<File> dirs,
			List<File> files) throws UnsupportedEncodingException, MalformedURLException {
		boolean isRoot = "/".equals(relativePath);
		var html = new String(template, "utf-8");
		html = html.replace("{{currentDir}}", currentDirName);

		var sb = new StringBuilder();
		if (!isRoot) {
			sb.append(makeLink("/..", "..", true));
		}

		appendList(sb, relativePath, dirs, true);
		appendList(sb, relativePath, files, false);

		html = html.replace("{{list}}", sb.toString());
		return html.getBytes();
	}

	private static void appendList(StringBuilder sb, String relativePath, List<File> files, boolean areDirs) {
		for (File file : files) {
			String path = !relativePath.equals("/") ? relativePath + file.getName() : file.getName();

			String name;
			if (areDirs) {
				name = "/" + file.getName();
				path += "/";
			} else {
				name = file.getName();
			}

			sb.append(makeLink(name, path, areDirs));
		}
	}

	private static String makeLink(String name, String path, boolean isDir) {
		StringBuilder b = new StringBuilder();
		b.append("<a href='");
		b.append(path);
		b.append("'");
		if (!isDir) {
			b.append(" target='_blank'");
		}
		b.append(">");
		b.append(name);
		b.append("</a><br>");

		return b.toString();
	}
}
