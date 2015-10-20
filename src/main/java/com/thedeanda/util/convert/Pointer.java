package com.thedeanda.util.convert;

import java.io.Serializable;

public class Pointer<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	public T value;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Pointer() {

	}

	public Pointer(T value) {
		this.value = value;
	}
}
