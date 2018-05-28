package com.vorheim.httpserver;

import java.io.File;

public class FileScanner {

	private File userDir;

	public FileScanner() {
		// new java.io.File(".").getCanonicalPath();
		userDir = new File(System.getProperty("user.dir"));
	}

	public File getCurrentFile(String relativePath) {
		return new File(userDir.getAbsolutePath() + File.separatorChar + relativePath);
	}

}
