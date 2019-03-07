package com.thedeanda.util.convert.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedeanda.util.convert.ConversionListener;
import com.thedeanda.util.convert.FileConverter;
import com.thedeanda.util.convert.fileinfo.AudioFileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfoListener;
import com.thedeanda.util.convert.fileinfo.FileInfoReader;
import com.thedeanda.util.convert.fileinfo.ImageFileInfo;
import com.thedeanda.util.process.ProcessResult;
import com.thedeanda.util.process.RunExec;

public class AudioConvertor implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(AudioConvertor.class);

	private AudioFileInfo file;
	private AudioProperties properties;
	private ConversionListener<AudioFileInfo> listener;
	private FileConverter fileConverter;

	private AudioFileInfo fileInfo = null;

	public AudioConvertor(FileConverter fileConverter, AudioFileInfo file, AudioProperties properties,
			ConversionListener<AudioFileInfo> listener) {
		this.fileConverter = fileConverter;
		this.file = file;
		this.properties = properties;
		this.listener = listener;
	}

	@Override
	public void run() {
		File file = this.file.getFile();
		File directory = file.getParentFile();

		File outputFile = null;
		boolean failed = true;
		try {
			outputFile = File.createTempFile(properties.getTargetBaseFilename(), "." + properties.getCodec().getExt(),
					fileConverter.getTempDir());

			List<String> command = new ArrayList<String>();
			command.add(fileConverter.getFfmpeg());
			command.add("-y");
			command.add("-i");
			command.add(file.getName());

			// add params here
			addParamChannels(command, properties);
			addParamAudioRate(command, properties);
			addParamAudioQuality(command, properties);

			command.add(outputFile.getAbsolutePath());

			RunExec runExec = fileConverter.getRunExec();
			ProcessResult pr = runExec.exec(command, directory);
			if (pr.getResult() != 0 || !outputFile.exists() || outputFile.length() <= 0) {
				log.debug("result from audio convrsion: " + pr.getResult());
				failed = true;
			}
			failed = false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			failed = true;
		}

		if (!failed) {
			// now lets read file info...
			FileInfoReader r = new FileInfoReader(this.fileConverter, outputFile);
			fileInfo = (AudioFileInfo) r.call();

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
			List<AudioFileInfo> files = new ArrayList<>();
			files.add(fileInfo);
			listener.complete(files);
		}
	}

	private void addParamCodec(List<String> command, AudioProperties properties) {
		AudioCodec val = properties.getCodec();
		command.add("-c:a");
		switch (val) {
		case MP3:
			command.add("libmp3lame");
			break;
		case Vorbis:
			command.add("libvorbis");
			break;
		}
	}

	private void addParamChannels(List<String> command, AudioProperties properties) {
		Integer val = properties.getAudioChannels();
		if (val != null && val >= 1 && val <= 2) {
			command.add("-ac");
			command.add(String.valueOf(val));
		}
	}

	private void addParamAudioRate(List<String> command, AudioProperties properties) {
		Integer val = properties.getAudioRate();
		if (val != null && val >= 1) {
			command.add("-ar");
			command.add(String.valueOf(val));
		}
	}

	private void addParamAudioQuality(List<String> command, AudioProperties properties) {
		Integer val = properties.getAudioQuality();
		if (val != null && val >= 1 && val <= 9) {
			command.add("-q:a");
			command.add(String.valueOf(val));
		}
	}
}
