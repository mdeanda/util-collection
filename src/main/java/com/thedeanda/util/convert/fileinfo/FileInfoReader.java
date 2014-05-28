package com.thedeanda.util.convert.fileinfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.util.convert.FileConverter;
import com.thedeanda.util.process.ProcessResult;
import com.thedeanda.util.process.RunExec;

public class FileInfoReader implements Runnable {
	private static final Logger log = LoggerFactory
			.getLogger(FileInfoReader.class);

	private FileConverter fileConverter;
	private File file;
	private FileInfoListener listener;
	private static final int MAX_DURATION = 10000;

	private static final Pattern sizePatternFromFileCmd = Pattern
			.compile("^.*\\s+(\\d+) x (\\d+)([^0-9].*)?$");
	private static final Pattern sizePatternFromIdentifyCmd = Pattern
			.compile("^.*\\s+(\\d+)x(\\d+)\\+.*$");

	public FileInfoReader(FileConverter fc, File file, FileInfoListener listener) {
		log.trace("new file info reader");
		this.fileConverter = fc;
		this.file = file;
		this.listener = listener;
	}

	@Override
	public void run() {
		FileInfo fileInfo = null;
		try {
			String fileInfoLine = runFileCommand();
			fileInfo = fileId(fileInfoLine);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} finally {
			// TODO: if fileInfo is null, create a generic one here
			listener.fileInfoReady(fileInfo);
		}
	}

	private String runFileCommand() throws IOException {
		List<String> command = new ArrayList<String>();
		command.add(fileConverter.getFile());
		command.add(file.getName());

		ProcessResult result = RunExec.exec(command, file.getParentFile(),
				MAX_DURATION);
		if (result != null && !StringUtils.isBlank(result.getStdOutput())) {
			return result.getStdOutput().trim();
		}

		return null;
	}

	private FileInfo fileId(String line) throws IOException {
		log.debug(line);

		if (StringUtils.isBlank(line))
			return null;

		int colon = line.indexOf(':');
		if (colon < 0)
			return null;
		line = line.substring(colon + 1).trim();

		if ("ASCII text".equals(line)) {
			return readTextFileInfo(line);
		} else if ("UTF-8 Unicode text".equals(line)) {
			return readTextFileInfo(line);
		} else if (line.contains("JPEG")) {
			return readImageFileInfo(line);
		} else if (line.contains("GIF")) {
			return readImageFileInfo(line);
		} else if (line.contains("PNG")) {
			return readImageFileInfo(line);
		}

		return null;
	}

	private FileInfo readTextFileInfo(String line) {
		TextFileInfo ret = new TextFileInfo();
		ret.setFile(file);
		return ret;
	}

	private FileInfo readImageFileInfo(String line) throws IOException {
		ImageFileInfo ret = new ImageFileInfo();
		ret.setFile(file);

		boolean hasSize = false;
		try {
			Matcher matcher = sizePatternFromFileCmd.matcher(line);
			if (matcher.matches()) {
				int w = Integer.parseInt(matcher.group(1));
				int h = Integer.parseInt(matcher.group(2));
				ret.setWidth(w);
				ret.setHeight(h);
				hasSize = true;
			}
		} catch (NumberFormatException nfe) {
			hasSize = false;
		}

		if (!hasSize) {
			// use another cmd line program to figure out the size
			String output = readImageSizeViaCommand();
			try {
				Matcher matcher = sizePatternFromIdentifyCmd.matcher(output);
				if (matcher.matches()) {
					int w = Integer.parseInt(matcher.group(1));
					int h = Integer.parseInt(matcher.group(2));
					ret.setWidth(w);
					ret.setHeight(h);
					hasSize = true;
				}
			} catch (NumberFormatException nfe) {
				hasSize = false;
			}
		}

		return ret;
	}

	private String readImageSizeViaCommand() throws IOException {
		List<String> command = new ArrayList<String>();
		command.add(fileConverter.getIdentify());
		command.add(file.getName());

		ProcessResult result = RunExec.exec(command, file.getParentFile(),
				MAX_DURATION);
		if (result != null && !StringUtils.isBlank(result.getStdOutput())) {
			return result.getStdOutput().trim();
		}

		return null;
	}

}
