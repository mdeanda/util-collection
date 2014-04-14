package com.thedeanda.util.convert.fileinfo;

import java.io.File;

import com.thedeanda.util.convert.FileConverter;

public class FileInfoReader implements Runnable {
	private FileConverter fileConverter;
	private File file;
	private FileInfoListener listener;

	public FileInfoReader(FileConverter fc, File file, FileInfoListener listener) {
		this.fileConverter = fc;
		this.file = file;
		this.listener = listener;
	}

	@Override
	public void run() {

	}

}
