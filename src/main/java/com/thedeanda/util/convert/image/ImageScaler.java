package com.thedeanda.util.convert.image;

import com.thedeanda.util.convert.ConversionListener;
import com.thedeanda.util.convert.FileConverter;
import com.thedeanda.util.convert.fileinfo.FileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfoReader;
import com.thedeanda.util.convert.fileinfo.ImageFileInfo;
import com.thedeanda.util.process.ProcessResult;
import com.thedeanda.util.process.RunExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageScaler implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(ImageScaler.class);

	private static final String TEMP_BASENAME = "util-collection";

	private FileConverter fileConverter;
	private ImageFileInfo file;
	private ImageScaleParams params;
	private ConversionListener listener;

	private ImageFileInfo fileInfo = null;

	public ImageScaler(FileConverter fileConverter, ImageFileInfo file,
			ImageScaleParams params, ConversionListener listener) {
		this.fileConverter = fileConverter;
		this.file = file;
		this.params = params;
		this.listener = listener;
	}

	@Override
	public void run() {
		int height = params.getHeight();
		int width = params.getWidth();
		if (height < 0)
			height = 0;
		if (width < 0) {
			listener.failed();
			return;
		}

		File file = this.file.getFile();
		File directory = file.getParentFile();
		float aspect = this.file.getWidth() / (float) this.file.getHeight();
		if (height == 0) {
			// when height not set, don't grow width
			if (this.file.getWidth() < width)
				width = this.file.getWidth();
			height = (int) (width / aspect);
		}
		String sizeString = width + "x" + height;
		String fname = width + "x" + height + "."
				+ this.file.getThumbExtension();

		File outputFile = null;
		boolean failed = true;
		try {
			outputFile = File.createTempFile(TEMP_BASENAME, fname, fileConverter.getTempDir());

			List<String> command = new ArrayList<String>();
			command.add(fileConverter.getConvert());
			command.add(file.getName());
			command.add("-resize");
			command.add(sizeString + "^>");
			command.add("-gravity");
			command.add("center");
			command.add("-extent");
			command.add(sizeString);
			command.add(outputFile.getAbsolutePath());

			RunExec runExec = fileConverter.getRunExec();
			ProcessResult pr = runExec.exec(command, directory);
			if (pr.getResult() != 0 || !outputFile.exists()
					|| outputFile.length() <= 0) {
				log.warn("result from file resize: " + pr.getResult());
				failed = true;
			}
			failed = false;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			failed = true;
		}

		if (!failed) {
			// now lets read file info...
			//TODO: verify that we always get an ImageFileInfo instance here
			FileInfoReader r = new FileInfoReader(this.fileConverter, outputFile);
			fileInfo = (ImageFileInfo) r.call();

			if (fileInfo == null) {
				failed = true;
			}
		}

		if (failed) {
			if (outputFile != null && outputFile.exists()) {
				outputFile.delete();
			}
			listener.failed();
		} else {
			List<FileInfo> files = new ArrayList<FileInfo>();
			files.add(fileInfo);
			listener.complete(files);
		}
	}
}
