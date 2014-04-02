package com.thedeanda.util.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableStreamReader implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(RunnableStreamReader.class);

	private InputStream is;
	private String charSet;
	private StringWriter sw;

	private CountDownLatch startLatch;

	private CountDownLatch endLatch;

	RunnableStreamReader(InputStream is, CountDownLatch latch,
			CountDownLatch endLatch) {
		this.is = is;
		charSet = "UTF-8";
		sw = new StringWriter();
		this.startLatch = latch;
		this.endLatch = endLatch;
	}

	@Override
	public void run() {
		InputStreamReader isr = null;
		try {
			startLatch.countDown();
			isr = new InputStreamReader(is, charSet);
			IOUtils.copy(isr, sw);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			endLatch.countDown();
			IOUtils.closeQuietly(isr);
			IOUtils.closeQuietly(is);
		}
	}

	public String getOutput() {
		return sw.toString();
	}

}
