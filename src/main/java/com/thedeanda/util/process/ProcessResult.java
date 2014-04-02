package com.thedeanda.util.process;

public class ProcessResult {
	private String errOutput;
	private String stdOutput;
	private int result;
	private long duration;

	public ProcessResult(String output, String errOutput, int result,
			long duration) {
		this.stdOutput = output;
		this.result = result;
		this.duration = duration;
		this.errOutput = errOutput;
	}

	public String getErrOutput() {
		return errOutput;
	}

	public String getStdOutput() {
		return stdOutput;
	}

	public int getResult() {
		return result;
	}

	public long getDuration() {
		return duration;
	}

}
