package com.vorheim.httpserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

public class ArgumentParserTest {

	private static ArgumentsParser ap;

	@BeforeClass
	public static void init() {
		ap = new ArgumentsParser(new String[] { "/Users", "-p", "1234" });
	}

	@Test
	public void testHasErrors() {
		assertFalse(new ArgumentsParser(new String[0]).hasErrors());
		assertTrue(new ArgumentsParser(new String[] { "" }).hasErrors());
	}

	@Test
	public void testGetPort() {
		assertEquals(1234, ap.getPort());
	}

	@Test
	public void testGetMainDir() {
		assertEquals(new File("/Users"), ap.getMainDir());
	}

}
