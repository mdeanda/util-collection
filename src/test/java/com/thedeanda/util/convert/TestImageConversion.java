package com.thedeanda.util.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thedeanda.util.convert.fileinfo.FileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfoListener;
import com.thedeanda.util.convert.fileinfo.ImageFileInfo;
import com.thedeanda.util.convert.image.ImageScaleParams;

public class TestImageConversion {
	private FileConverter fc;
	private List<FileInfo> files;

	@Before
	public void setup() {
		fc = new FileConverter();
		files = new ArrayList<FileInfo>();
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
	public void testPng() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/mini_r8_car.png");
		fc.readFileInfo(file, new FileInfoListener() {
			@Override
			public void fileInfoReady(FileInfo fileInfo) {
				holder.fileInfo = fileInfo;
				latch.countDown();
			}
		});

		if (!latch.await(10, TimeUnit.SECONDS)) {
			fail("file info never ready");
		}

		FileInfo fileInfo = holder.fileInfo;
		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof ImageFileInfo);

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(400, fi.getWidth());
		assertEquals(322, fi.getHeight());

		// ok seems ok, lets scale image
		final CountDownLatch latch2 = new CountDownLatch(1);
		ImageScaleParams params = new ImageScaleParams(200);
		fc.convertResize(fi, params, new ConversionListener() {
			@Override
			public void failed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void complete(List<FileInfo> files) {
				TestImageConversion.this.files.addAll(files);
				latch2.countDown();
			}
		});
		if (!latch2.await(10, TimeUnit.SECONDS)) {
			fail("conversion failed or took too long");
		}

		assertNotNull(files);
		assertEquals("file count is off", 1, files.size());
		ImageFileInfo fi2 = (ImageFileInfo) files.get(0);
		assertTrue("file exists", fi.getFile().exists());
		assertEquals(200, fi2.getWidth());
		assertEquals(161, fi2.getHeight());
	}

	@Test
	public void testPngUpscale() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/mini_r8_car.png");
		fc.readFileInfo(file, new FileInfoListener() {
			@Override
			public void fileInfoReady(FileInfo fileInfo) {
				holder.fileInfo = fileInfo;
				latch.countDown();
			}
		});

		if (!latch.await(10, TimeUnit.SECONDS)) {
			fail("file info never ready");
		}

		FileInfo fileInfo = holder.fileInfo;
		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof ImageFileInfo);

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(400, fi.getWidth());
		assertEquals(322, fi.getHeight());

		// ok seems ok, lets scale image
		final CountDownLatch latch2 = new CountDownLatch(1);
		ImageScaleParams params = new ImageScaleParams(800);
		fc.convertResize(fi, params, new ConversionListener() {
			@Override
			public void failed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void complete(List<FileInfo> files) {
				TestImageConversion.this.files.addAll(files);
				latch2.countDown();
			}
		});
		if (!latch2.await(10, TimeUnit.SECONDS)) {
			fail("conversion failed or took too long");
		}

		//upscaled images come back at same size by default...
		assertNotNull(files);
		assertEquals("file count is off", 1, files.size());
		ImageFileInfo fi2 = (ImageFileInfo) files.get(0);
		assertTrue("file exists", fi.getFile().exists());
		assertEquals(400, fi2.getWidth());
		assertEquals(322, fi2.getHeight());
	}

	@Test
	public void testPngHeight() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/mini_r8_car.png");
		fc.readFileInfo(file, new FileInfoListener() {
			@Override
			public void fileInfoReady(FileInfo fileInfo) {
				holder.fileInfo = fileInfo;
				latch.countDown();
			}
		});

		if (!latch.await(10, TimeUnit.SECONDS)) {
			fail("file info never ready");
		}

		FileInfo fileInfo = holder.fileInfo;
		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof ImageFileInfo);

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(400, fi.getWidth());
		assertEquals(322, fi.getHeight());

		// ok seems ok, lets scale image
		final CountDownLatch latch2 = new CountDownLatch(1);
		ImageScaleParams params = new ImageScaleParams(0, 40);
		fc.convertResize(fi, params, new ConversionListener() {
			@Override
			public void failed() {
				// TODO Auto-generated method stub

			}

			@Override
			public void complete(List<FileInfo> files) {
				TestImageConversion.this.files.addAll(files);
				latch2.countDown();
			}
		});
		if (!latch2.await(10, TimeUnit.SECONDS)) {
			fail("conversion failed or took too long");
		}

		assertNotNull(files);
		assertEquals("file count is off", 1, files.size());
		ImageFileInfo fi2 = (ImageFileInfo) files.get(0);
		assertTrue("file exists", fi.getFile().exists());
		assertEquals(50, fi2.getWidth());
		assertEquals(40, fi2.getHeight());
	}

}
