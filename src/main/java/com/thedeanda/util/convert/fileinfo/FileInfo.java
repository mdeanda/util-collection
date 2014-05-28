package com.thedeanda.util.convert.fileinfo;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.FilenameUtils;

abstract public class FileInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getExtension() {
		String ext = null;
		if (getFile() != null) {
			ext = FilenameUtils.getExtension(getFile().getName());
		}

		return ext;
	}
}
