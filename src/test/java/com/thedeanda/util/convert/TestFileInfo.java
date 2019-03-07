package com.thedeanda.util.convert;

import com.thedeanda.util.convert.fileinfo.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class TestFileInfo {
	private FileConverter fc;

	@Before
	public void setup() {
		fc = new FileConverter();
		fc.setTempDir(new File("tmp"));
	}

	@Test
	public void testPlainText() throws InterruptedException, TimeoutException, ExecutionException {
		FileInfo fileInfo = null;

		File file = new File("src/test/resources/lorem.txt");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

		assertNotNull(fileInfo);
		assertEquals(file, fileInfo.getFile());

		TextFileInfo fi = (TextFileInfo) fileInfo;
		// TODO: finish
	}

	@Test
	public void testPng() throws InterruptedException, TimeoutException, ExecutionException {
		FileInfo fileInfo = null;

		File file = new File("src/test/resources/mini_r8_car.png");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

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
	public void testJpg() throws InterruptedException, TimeoutException, ExecutionException {
		FileInfo fileInfo = null;

		File file = new File("src/test/resources/Baby-taco.jpg");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

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
	public void testGif() throws InterruptedException, TimeoutException, ExecutionException {
		FileInfo fileInfo = null;

		File file = new File(
				"src/test/resources/0fd479da894756522251fc29f1af2bd1.gif");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

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
	public void testMp3() throws InterruptedException, TimeoutException, ExecutionException {
		FileInfo fileInfo = null;

		File file = new File("src/test/resources/06-Radiohead-FaustArp.mp3");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof AudioFileInfo);
		assertEquals(file, fileInfo.getFile());

		AudioFileInfo fi = (AudioFileInfo) fileInfo;
		assertEquals("mp3", fi.getExtension());
		assertEquals(AudioEncoding.MP3, fi.getEncoding());
	}

	@Test
	public void testOggVorbis() throws InterruptedException, TimeoutException, ExecutionException {
		FileInfo fileInfo = null;

		File file = new File("src/test/resources/Beck-DeadWildCat.ogg");
		fileInfo = fc.readFileInfo(file).get(10, TimeUnit.SECONDS);

		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof AudioFileInfo);
		assertEquals(file, fileInfo.getFile());

		AudioFileInfo fi = (AudioFileInfo) fileInfo;
		assertEquals("ogg", fi.getExtension());
		assertEquals(AudioEncoding.VORBIS, fi.getEncoding());
	}
}
