package com.thedeanda.util.convert;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.thedeanda.util.convert.fileinfo.FileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfoListener;
import com.thedeanda.util.convert.fileinfo.FileInfoReader;
import com.thedeanda.util.convert.fileinfo.ImageFileInfo;
import com.thedeanda.util.convert.image.ImageScaleParams;
import com.thedeanda.util.convert.image.ImageScaler;
import com.thedeanda.util.domain.Pointer;

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

	public FileInfo readFileInfoInProcess(File file) {
		final Pointer<FileInfo> ret = new Pointer<FileInfo>();
		FileInfoReader fir = new FileInfoReader(this, file,
				new FileInfoListener() {

					@Override
					public void fileInfoReady(FileInfo fileInfo) {
						ret.value = fileInfo;
					}
				});
		fir.run();

		return ret.value;
	}

	public void convertResize(ImageFileInfo file, ImageScaleParams params,
			ConversionListener listener) {
		executor.execute(new ImageScaler(this, file, params, listener));
	}

	public List<FileInfo> convertResizeInProcess(ImageFileInfo file,
			ImageScaleParams params) {
		final Pointer<List<FileInfo>> ret = new Pointer<List<FileInfo>>();
		ImageScaler is = new ImageScaler(this, file, params,
				new ConversionListener() {

					@Override
					public void failed() {
						ret.value = null;
					}

					@Override
					public void complete(List<FileInfo> files) {
						ret.value = files;
					}
				});
		is.run();
		return ret.value;
	}
}
