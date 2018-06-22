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

import com.thedeanda.util.convert.audio.AudioCodec;
import com.thedeanda.util.convert.audio.AudioProperties;
import com.thedeanda.util.convert.fileinfo.AudioEncoding;
import com.thedeanda.util.convert.fileinfo.AudioFileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfo;
import com.thedeanda.util.convert.fileinfo.FileInfoListener;

public class TestAudioConversion {

	private FileConverter convertor;
	private List<File> files;

	@Before
	public void init() {
		convertor = new FileConverter();
		convertor.setTempDir(new File("tmp"));
		files = new ArrayList<>();
	}

	@After
	public void done() {
		for (File file : files) {
			file.delete();
		}
	}

	@Test
	public void testMp3ToOgg() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/06-Radiohead-FaustArp.mp3");
		convertor.readFileInfo(file, new FileInfoListener() {
			@Override
			public void fileInfoReady(FileInfo fileInfo) {
				holder.fileInfo = fileInfo;
				latch.countDown();
			}
		});

		if (!latch.await(10, TimeUnit.SECONDS)) {
			fail("file info never ready");
		}

		AudioFileInfo fileInfo = (AudioFileInfo) holder.fileInfo;
		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof AudioFileInfo);

		final CountDownLatch latch2 = new CountDownLatch(1);
		AudioProperties properties = new AudioProperties();
		properties.setCodec(AudioCodec.Vorbis);
		properties.setTargetBaseFilename("radiohead");
		final List<AudioFileInfo> generatedFiles = new ArrayList<>();
		convertor.convertAudio(fileInfo, properties, new ConversionListener<AudioFileInfo>() {
			@Override
			public void failed() {
				latch2.countDown();
				fail("failed to convert audio");
			}

			@Override
			public void complete(List<AudioFileInfo> convertedFiles) {
				for (AudioFileInfo afi : convertedFiles) {
					files.add(afi.getFile());
				}
				generatedFiles.addAll(convertedFiles);
				latch2.countDown();
			}
		});
		if (!latch2.await(30, TimeUnit.SECONDS)) {
			fail("conversion failed or took too long");
		}

		assertNotNull(generatedFiles);
		assertEquals("file count is off", 1, generatedFiles.size());
		AudioFileInfo fi2 = generatedFiles.get(0);
		assertTrue("file exists", fi2.getFile().exists());
		assertEquals(AudioEncoding.VORBIS, fi2.getEncoding());
		// assertEquals(40, fi2.getLengthInHundredths());
	}
	

	@Test
	public void testOggToMp3() throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final FileInfoHolder holder = new FileInfoHolder();

		File file = new File("src/test/resources/Beck-DeadWildCat.ogg");
		convertor.readFileInfo(file, new FileInfoListener() {
			@Override
			public void fileInfoReady(FileInfo fileInfo) {
				holder.fileInfo = fileInfo;
				latch.countDown();
			}
		});

		if (!latch.await(10, TimeUnit.SECONDS)) {
			fail("file info never ready");
		}

		AudioFileInfo fileInfo = (AudioFileInfo) holder.fileInfo;
		assertNotNull(fileInfo);
		assertTrue(fileInfo instanceof AudioFileInfo);

		final CountDownLatch latch2 = new CountDownLatch(1);
		AudioProperties properties = new AudioProperties();
		properties.setCodec(AudioCodec.MP3);
		properties.setTargetBaseFilename("beck");
		final List<AudioFileInfo> generatedFiles = new ArrayList<>();
		convertor.convertAudio(fileInfo, properties, new ConversionListener<AudioFileInfo>() {
			@Override
			public void failed() {
				latch2.countDown();
				fail("failed to convert audio");
			}

			@Override
			public void complete(List<AudioFileInfo> convertedFiles) {
				for (AudioFileInfo afi : convertedFiles) {
					files.add(afi.getFile());
				}
				generatedFiles.addAll(convertedFiles);
				latch2.countDown();
			}
		});
		if (!latch2.await(30, TimeUnit.SECONDS)) {
			fail("conversion failed or took too long");
		}

		assertNotNull(generatedFiles);
		assertEquals("file count is off", 1, generatedFiles.size());
		AudioFileInfo fi2 = generatedFiles.get(0);
		assertTrue("file exists", fi2.getFile().exists());
		assertEquals(AudioEncoding.MP3, fi2.getEncoding());
		// assertEquals(40, fi2.getLengthInHundredths());
	}
}
