package com.thedeanda.util.convert.audio;

public enum AudioCodec {
	MP3("mp3"), Vorbis("ogg");

	private String ext;

	private AudioCodec(String ext) {
		this.ext = ext;
	}

	public String getExt() {
		return ext;
	}

}
