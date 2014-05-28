package com.thedeanda.util.convert.fileinfo;

public class AudioFileInfo extends FileInfo {
	private static final long serialVersionUID = 1L;
	private AudioEncoding encoding;
	private int lengthInHundredths;

	public AudioEncoding getEncoding() {
		return encoding;
	}

	public void setEncoding(AudioEncoding encoding) {
		this.encoding = encoding;
	}

	public int getLengthInHundredths() {
		return lengthInHundredths;
	}

	public void setLengthInHundredths(int lengthInHundredths) {
		this.lengthInHundredths = lengthInHundredths;
	}

}
