package com.thedeanda.util.convert.fileinfo;


public class AudioFileInfo extends FileInfo {
	private static final long serialVersionUID = 1L;
	private AudioEncoding encoding;
	private int lengthInMillis;

	public AudioEncoding getEncoding() {
		return encoding;
	}

	public void setEncoding(AudioEncoding encoding) {
		this.encoding = encoding;
	}

	public int getLengthInMillis() {
		return lengthInMillis;
	}

	public void setLengthInMillis(int lengthInMillis) {
		this.lengthInMillis = lengthInMillis;
	}
}
