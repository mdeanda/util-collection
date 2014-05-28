package com.thedeanda.util.convert;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.thedeanda.util.convert.fileinfo.FileInfoListener;
import com.thedeanda.util.convert.fileinfo.FileInfoReader;
import com.thedeanda.util.convert.fileinfo.ImageFileInfo;
import com.thedeanda.util.convert.image.ImageScaleParams;
import com.thedeanda.util.convert.image.ImageScaler;

public class FileConverter {
	private String file = "/usr/bin/file";
	private String ffmpeg = "/usr/bin/ffmpeg";
	private String identify = "/usr/bin/identify";
	private String convert = "/usr/bin/convert";

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

	public String getConvert() {
		return convert;
	}

	public void setConvert(String convert) {
		this.convert = convert;
	}

	public void readFileInfo(File file, FileInfoListener listener) {
		executor.execute(new FileInfoReader(this, file, listener));
	}

	public void convertResize(ImageFileInfo file, ImageScaleParams params,
			ConversionListener listener) {
		executor.execute(new ImageScaler(this, file, params, listener));
	}
}
