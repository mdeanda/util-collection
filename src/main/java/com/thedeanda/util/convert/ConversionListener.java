package com.thedeanda.util.convert;

import java.util.List;

import com.thedeanda.util.convert.fileinfo.FileInfo;

public interface ConversionListener {
	public void failed();

	public void complete(List<FileInfo> files);
}
