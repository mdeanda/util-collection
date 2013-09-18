package com.thedeanda.util.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

//TODO: move this to a utils folder
public class RunExec {

	public static ProcessResult exec(List<String> command, File directory)
			throws IOException {
		StringBuilder ret = new StringBuilder();
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		if (directory != null)
			pb.directory(directory);

		Process process = pb.start();

		// Read out dir output
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			ret.append(line);
			ret.append("\n");
		}

		// Wait to get exit value
		int exitValue;
		long duration = 0;
		try {
			long start = System.currentTimeMillis();
			exitValue = process.waitFor();
			long end = System.currentTimeMillis();
			duration = end - start;
		} catch (InterruptedException e) {
			exitValue = -1;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ProcessResult(ret.toString(), exitValue, duration);
	}
}
