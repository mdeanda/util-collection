package com.thedeanda.util.convert.fileinfo;

import org.apache.commons.lang3.StringUtils;

public class ImageFileInfo extends FileInfo {
	private static final long serialVersionUID = 1L;

	private int width;
	private int height;

	public String getThumbExtension() {
		String ext = getExtension();
		if (StringUtils.isBlank(ext))
			ext = "jpg";
		ext = ext.toLowerCase();

		boolean extOk = false;
		String[] allowedExtensions = new String[] { "jpg", "gif", "png" };
		for (String ae : allowedExtensions) {
			if (ae.equals(ext)) {
				extOk = true;
				break;
			}
		}
		if (!extOk) {
			ext = "jpg";
		}

		return ext;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
