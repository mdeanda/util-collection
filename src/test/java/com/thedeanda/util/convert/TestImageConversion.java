package com.thedeanda.util.convert;

import com.thedeanda.util.convert.fileinfo.FileInfo;
import com.thedeanda.util.convert.fileinfo.ImageFileInfo;
import com.thedeanda.util.convert.image.ImageScaleParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class TestImageConversion {
	private FileConverter fc;
	private List<ImageFileInfo> files;

	@Before
	public void setup() {
		fc = new FileConverter();
		fc.setTempDir(new File("tmp"));
		files = new ArrayList<>();
	}

	@After
	public void cleanup() {
		if (files != null) {
			for (FileInfo fi : files) {
				fi.getFile().delete();
			}
		}
	}

	@Test
	public void testPng() throws InterruptedException, TimeoutException, ExecutionException {
		final CountDownLatch latch = new CountDownLatch(1);
		FileInfo fileInfo = null;

		File file = new File("src/test/resources/mini_r8_car.png");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof ImageFileInfo);

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(400, fi.getWidth());
		assertEquals(322, fi.getHeight());

		// ok seems ok, lets scale image
		ImageScaleParams params = new ImageScaleParams(200);
		ImageFileInfo result = fc.convertResize(fi, params).get();
		files.add(result);

		assertNotNull(files);
		assertEquals("file count is off", 1, files.size());
		ImageFileInfo fi2 = (ImageFileInfo) files.get(0);
		assertTrue("file exists", fi.getFile().exists());
		assertEquals(200, fi2.getWidth());
		assertEquals(161, fi2.getHeight());
	}

	@Test
	public void testPngUpscale() throws InterruptedException, TimeoutException, ExecutionException {
		FileInfo fileInfo = null;

		File file = new File("src/test/resources/mini_r8_car.png");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof ImageFileInfo);

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(400, fi.getWidth());
		assertEquals(322, fi.getHeight());

		// ok seems ok, lets scale image
		ImageScaleParams params = new ImageScaleParams(800);
		ImageFileInfo result = fc.convertResize(fi, params).get();
		files.add(result);

		//upscaled images come back at same size by default...
		assertNotNull(files);
		assertEquals("file count is off", 1, files.size());
		ImageFileInfo fi2 = (ImageFileInfo) files.get(0);
		assertTrue("file exists", fi.getFile().exists());
		assertEquals(400, fi2.getWidth());
		assertEquals(322, fi2.getHeight());
	}

	@Test
	public void testPngHeight() throws InterruptedException, TimeoutException, ExecutionException {
		FileInfo fileInfo = null;

		File file = new File("src/test/resources/mini_r8_car.png");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof ImageFileInfo);

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(400, fi.getWidth());
		assertEquals(322, fi.getHeight());

		// ok seems ok, lets scale image
		ImageScaleParams params = new ImageScaleParams(0, 40);
		ImageFileInfo result = fc.convertResize(fi, params).get();
		files.add(result);

		assertNotNull(files);
		assertEquals("file count is off", 1, files.size());
		ImageFileInfo fi2 = (ImageFileInfo) files.get(0);
		assertTrue("file exists", fi.getFile().exists());
		assertEquals(50, fi2.getWidth());
		assertEquals(40, fi2.getHeight());
	}

}
