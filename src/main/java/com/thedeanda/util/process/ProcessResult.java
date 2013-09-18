package com.thedeanda.util.process;

public class ProcessResult {
	private String output;
	private int result;
	private long duration;

	public ProcessResult(String output, int result, long duration) {
		this.output = output;
		this.result = result;
		this.duration = duration;
	}

	public String getOutput() {
		return output;
	}

	public int getResult() {
		return result;
	}

	public long getDuration() {
		return duration;
	}
}
