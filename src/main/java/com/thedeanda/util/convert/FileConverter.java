package com.thedeanda.util.convert;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.thedeanda.util.convert.fileinfo.FileInfoListener;
import com.thedeanda.util.convert.fileinfo.FileInfoReader;

public class FileConverter {
	private String ffmpeg;
	private String identify;
	private String file;
	private ExecutorService executor;

	public FileConverter() {
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime()
				.availableProcessors(), new ThreadFactory() {
			public Thread newThread(Runnable r) {
				return new Thread(r, "FileConverterThread");
			}
		});
	}

	public String getFfmpeg() {
		return ffmpeg;
	}

	public void setFfmpeg(String ffmpeg) {
		this.ffmpeg = ffmpeg;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void readFileInfo(File file, FileInfoListener listener) {
		executor.execute(new FileInfoReader(this, file, listener));
	}

}
