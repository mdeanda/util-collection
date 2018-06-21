package com.thedeanda.util.convert;

import java.util.List;

import com.thedeanda.util.convert.fileinfo.FileInfo;

public interface ConversionListener<T extends FileInfo> {
	public void failed();

	public void complete(List<T> files);
}
