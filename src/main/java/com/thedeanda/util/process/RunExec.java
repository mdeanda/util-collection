package com.thedeanda.util.process;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunExec {
	private static final Logger log = LoggerFactory.getLogger(RunExec.class);
	
	private File tempDir = null;

	public ProcessResult exec(String command, File directory) throws IOException {
		return exec(command, directory, 0);
	}

	public ProcessResult exec(String command, File directory, int maxDuration) throws IOException {
		String[] parts = StringUtils.split(command);
		List<String> list = Arrays.asList(parts);
		return exec(list, directory, maxDuration);
	}

	public ProcessResult exec(List<String> command, File directory) throws IOException {
		return exec(command, directory, 0);
	}

	/**
	 * 
	 * @param command
	 * @param directory
	 * @param maxDuration
	 *            - maximum amount of time in ms to let the command run before
	 *            aborting it
	 * @return
	 * @throws IOException
	 */
	public ProcessResult exec(List<String> command, File directory, long maxDuration) throws IOException {

		File stdOutFile = null;
		File errOutFile = null;

		// Wait to get exit value
		final IntegerHolder exitValue = new IntegerHolder();
		long duration = 0;
		Thread waitT = null;
		String stdOut = null;
		String errOut = null;
		try {
			// TODO: allow passing in a temp file directory
			stdOutFile = File.createTempFile("runexec_", ".std", tempDir);
			errOutFile = File.createTempFile("runexec_", ".err", tempDir);

			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectError(errOutFile);
			pb.redirectOutput(stdOutFile);

			// pb.redirectErrorStream(true);
			if (directory != null)
				pb.directory(directory);

			final Process process = pb.start();
			long start = System.currentTimeMillis();
			// make sure all threads start before timing for stop

			if (maxDuration <= 0) {
				exitValue.value = process.waitFor();
			} else {
				final CountDownLatch doneSignal = new CountDownLatch(1);
				waitT = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							exitValue.value = process.waitFor();
						} catch (InterruptedException e) {
							log.warn(e.getMessage(), e);
						}
						doneSignal.countDown();
					}
				});
				waitT.start();
				// wait for thread to finish...
				if (!doneSignal.await(maxDuration, TimeUnit.MILLISECONDS)) {
					// stop process
					log.warn("process took too long, aborting");
					process.destroy();
					waitT.interrupt();
					exitValue.value = -1;
				}
			}
			long end = System.currentTimeMillis();
			duration = end - start;

			stdOut = FileUtils.readFileToString(stdOutFile);
			errOut = FileUtils.readFileToString(errOutFile);

		} catch (InterruptedException e) {
			exitValue.value = -1;
			log.error(e.getMessage(), e);
		} finally {
			if (waitT != null)
				waitT.interrupt();
			if (stdOutFile != null)
				stdOutFile.delete();
			if (errOutFile != null)
				errOutFile.delete();
		}

		if (exitValue.value != 0) {
			log.warn("non-zero exit value: {} {}", exitValue.value, errOut);
			log.warn(StringUtils.join(command, " "));
		}

		return new ProcessResult(stdOut, errOut, exitValue.value, duration);
	}

	public File getTempDir() {
		return tempDir;
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

}
