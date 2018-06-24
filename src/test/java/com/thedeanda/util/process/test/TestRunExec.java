package com.thedeanda.util.process.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.thedeanda.util.process.ProcessResult;
import com.thedeanda.util.process.RunExec;

/**
 * these tests are platform dependent as they run commands like "ls", "sleep",
 * "watch" to test a few specific cases
 * 
 * @author mdeanda
 * 
 */
public class TestRunExec {
	@Test
	public void testBasic() throws Exception {
		List<String> command = Arrays.asList("ls");
		File directory = new File("src");
		ProcessResult results = new RunExec().exec(command, directory);

		assertEquals("exit code", 0, results.getResult());
		assertEquals("main\ntest\n", results.getStdOutput());
	}

	@Test
	public void testBasicWithMax() throws Exception {
		List<String> command = Arrays.asList("ls");
		File directory = new File("src");
		ProcessResult results = new RunExec().exec(command, directory, 10000);

		assertEquals("exit code", 0, results.getResult());
		assertEquals("main\ntest\n", results.getStdOutput());
	}

	@Test
	public void testBasicAsString() throws Exception {
		File directory = new File("src");
		ProcessResult results = new RunExec().exec("ls", directory);

		assertEquals("exit code", 0, results.getResult());
		assertEquals("main\ntest\n", results.getStdOutput());
	}

	@Test
	public void testSleep() throws Exception {
		List<String> command = Arrays.asList("sleep", "4");
		File directory = new File("src");
		ProcessResult results = new RunExec().exec(command, directory);

		assertEquals("exit code", 0, results.getResult());
		assertEquals("", results.getStdOutput());
	}

	@Test
	public void testSleepInterrupted() throws Exception {
		File directory = new File("src");
		ProcessResult results = new RunExec().exec("sleep 4", directory, 1000);

		assertTrue("exit code", 0 != results.getResult());
		assertEquals("", results.getStdOutput());
	}

}
