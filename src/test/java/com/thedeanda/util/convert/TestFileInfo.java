package com.thedeanda.util.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.thedeanda.util.convert.fileinfo.AudioEncoding;
import com.thedeanda.util.convert.fileinfo.AudioFileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfoListener;
import com.thedeanda.util.convert.fileinfo.ImageFileInfo;
import com.thedeanda.util.convert.fileinfo.TextFileInfo;

public class TestFileInfo {
	private FileConverter fc;

	@Before
	public void setup() {
		fc = new FileConverter();
	}

	@Test
	public void testPlainText() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/lorem.txt");
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
		assertEquals(file, fileInfo.getFile());

		TextFileInfo fi = (TextFileInfo) fileInfo;
		// TODO: finish
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
		assertEquals(file, fileInfo.getFile());

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(400, fi.getWidth());
		assertEquals(322, fi.getHeight());
		assertEquals("png", fi.getThumbExtension());
		assertEquals("png", fi.getExtension());
	}

	@Test
	public void testJpg() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/Baby-taco.jpg");
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
		assertEquals(file, fileInfo.getFile());

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(600, fi.getWidth());
		assertEquals(703, fi.getHeight());
		assertEquals("jpg", fi.getThumbExtension());
		assertEquals("jpg", fi.getExtension());
	}

	@Test
	public void testGif() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File(
				"src/test/resources/0fd479da894756522251fc29f1af2bd1.gif");
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
		assertEquals(file, fileInfo.getFile());

		ImageFileInfo fi = (ImageFileInfo) fileInfo;
		assertEquals(506, fi.getWidth());
		assertEquals(900, fi.getHeight());
		assertEquals("gif", fi.getThumbExtension());
		assertEquals("gif", fi.getExtension());
	}

	@Test
	public void testMp3() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/06-Radiohead-FaustArp.mp3");
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
		assertTrue(fileInfo instanceof AudioFileInfo);
		assertEquals(file, fileInfo.getFile());

		AudioFileInfo fi = (AudioFileInfo) fileInfo;
		assertEquals("mp3", fi.getExtension());
		assertEquals(AudioEncoding.MP3, fi.getEncoding());
	}

	@Test
	public void testOggVorbis() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/Beck-DeadWildCat.ogg");
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
		assertTrue(fileInfo instanceof AudioFileInfo);
		assertEquals(file, fileInfo.getFile());

		AudioFileInfo fi = (AudioFileInfo) fileInfo;
		assertEquals("ogg", fi.getExtension());
		assertEquals(AudioEncoding.VORBIS, fi.getEncoding());
	}
}
