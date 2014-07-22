package com.thedeanda.util.convert.image;

import java.io.Serializable;

public class ImageScaleParams implements Serializable {
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;

	public ImageScaleParams() {
		this(0, 0);
	}

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

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
