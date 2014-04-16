package com.thedeanda.util.convert.image;

public class ImageScaleParams {
	private int width;
	private int height;

	public ImageScaleParams(int width) {
		this(width, 0);
	}

	public ImageScaleParams(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
