package com.thedeanda.util.convert;

import com.thedeanda.util.convert.audio.AudioConvertor;
import com.thedeanda.util.convert.audio.AudioProperties;
import com.thedeanda.util.convert.fileinfo.AudioFileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfoReader;
import com.thedeanda.util.convert.fileinfo.ImageFileInfo;
import com.thedeanda.util.convert.image.ImageScaleParams;
import com.thedeanda.util.convert.image.ImageScaler;
import com.thedeanda.util.process.RunExec;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class FileConverter {
	private String file = "/usr/bin/file";
	private String ffmpeg = "/usr/bin/ffmpeg";
	private String identify = "/usr/bin/identify";
	private String convert = "/usr/bin/convert";
	private File tempDir = null;

	private ExecutorService executor;

	public FileConverter() {
		this(Runtime.getRuntime().availableProcessors());
	}

	public FileConverter(int threads) {
		this.executor = Executors.newFixedThreadPool(threads, new ThreadFactory() {
			public Thread newThread(Runnable r) {
				return new Thread(r, "FileConverterThread");
			}
		});
	}

	public RunExec getRunExec() {
		RunExec runExec = new RunExec();
		if (tempDir != null) {
			tempDir.mkdirs();
			runExec.setTempDir(tempDir);
		}

		return runExec;
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

	public Future<FileInfo> readFileInfo(File file) {
		return executor.submit(new FileInfoReader(this, file));
	}

	public void convertAudio(AudioFileInfo file, AudioProperties properties,
			ConversionListener<AudioFileInfo> listener) {
		executor.execute(new AudioConvertor(this, file, properties, listener));
	}

	public void convertResize(ImageFileInfo file, ImageScaleParams params, ConversionListener<ImageFileInfo> listener) {
		executor.execute(new ImageScaler(this, file, params, listener));
	}

	public List<ImageFileInfo> convertResizeInProcess(ImageFileInfo file, ImageScaleParams params) {
		final Pointer<List<ImageFileInfo>> ret = new Pointer<List<ImageFileInfo>>();
		ImageScaler is = new ImageScaler(this, file, params, new ConversionListener<ImageFileInfo>() {

			@Override
			public void failed() {
				ret.value = null;
			}

			@Override
			public void complete(List<ImageFileInfo> files) {
				ret.value = files;
			}
		});
		is.run();
		return ret.value;
	}

	public File getTempDir() {
		return tempDir;
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

}
