package com.thedeanda.util.convert.audio;

public class AudioProperties {
	private String targetBaseFilename;
	private Integer audioChannels; // 1, 2
	private Integer audioRate; // 22050, 44100, etc;
	private Integer audioQuality; // 0-9
	private AudioCodec codec;

	public String getTargetBaseFilename() {
		return targetBaseFilename;
	}

	public void setTargetBaseFilename(String targetBaseFilename) {
		this.targetBaseFilename = targetBaseFilename;
	}

	public Integer getAudioChannels() {
		return audioChannels;
	}

	public void setAudioChannels(Integer audioChannels) {
		this.audioChannels = audioChannels;
	}

	public Integer getAudioRate() {
		return audioRate;
	}

	public void setAudioRate(Integer audioRate) {
		this.audioRate = audioRate;
	}

	public Integer getAudioQuality() {
		return audioQuality;
	}

	public void setAudioQuality(Integer audioQuality) {
		this.audioQuality = audioQuality;
	}

	public AudioCodec getCodec() {
		return codec;
	}

	public void setCodec(AudioCodec codec) {
		this.codec = codec;
	}

}
