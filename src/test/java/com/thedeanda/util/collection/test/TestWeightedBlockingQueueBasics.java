package com.thedeanda.util.collection.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.thedeanda.util.collection.WeightedBlockingQueue;

public class TestWeightedBlockingQueueBasics {

	@Test
	public void testNullsNotSupported() throws Exception {
		WeightedBlockingQueue<String> q = new WeightedBlockingQueue<String>();
		try {
			q.add(null);
			fail("expected null pointer exception");
		} catch (NullPointerException ex) {
			// ignore, expected
		}
		try {
			q.add(null, 3);
			fail("expected null pointer exception");
		} catch (NullPointerException ex) {
			// ignore, expected
		}
		try {
			q.addAll(null);
			fail("expected null pointer exception");
		} catch (NullPointerException ex) {
			// ignore, expected
		}
	}

	@Test
	public void testQueueWithSingleElement() throws Exception {
		Queue<String> q = new WeightedBlockingQueue<String>();
		assertTrue("adding an item", q.add("a"));

		// check all the get variations
		assertEquals("size of queue", 1, q.size());
		assertEquals("a", q.element());
		assertEquals("size of queue", 1, q.size());
		assertEquals("a", q.peek());
		assertEquals("size of queue", 1, q.size());
		assertEquals("a", q.poll());
		assertEquals("size of queue", 0, q.size());
	}

	@Test
	public void testEmptyQueue() throws Exception {
		Queue<String> q = new WeightedBlockingQueue<String>();

		// check all the gets on empty queue
		assertNull("empty queuey .peek", q.peek());
		assertNull("empty queuey .poll", q.poll());
		assertEquals("empty size mismatch", 0, q.size());
		try {
			q.element();
			fail("expected no such element exception on .element");
		} catch (NoSuchElementException ex) {
			// ignore, expected
		}

		try {
			q.remove();
			fail("expected no such element exception on .remove");
		} catch (NoSuchElementException ex) {
			// ignore, expected
		}
	}

	@Test
	public void testQueueWithMultipleElements() throws Exception {
		WeightedBlockingQueue<String> q = new WeightedBlockingQueue<String>();
		assertTrue("adding an item", q.add("a"));
		assertTrue("adding an item", q.add("b"));
		assertTrue("adding an item", q.add("c"));
		assertTrue("adding an item", q.add("d"));
		assertTrue("adding an item", q.add("e"));
		assertTrue("adding an item", q.add("f"));
		assertTrue("adding an item", q.add("g"));
		assertTrue("adding an item", q.add("h"));
		assertTrue("adding an item", q.add("i"));
		assertTrue("adding an item", q.add("z", 10));

		// check all the get variations
		assertEquals("size of queue", 10, q.size());
		assertTrue("null", q.element() != null);
		assertEquals("size of queue", 10, q.size());
		assertTrue("null", q.peek() != null);
		assertEquals("size of queue", 10, q.size());

		// its highly unlikely that we'll get all a's before the z as its higher
		// priority
		while (!q.isEmpty()) {
			String val = q.poll();
			if ("z".equals(val) && q.isEmpty()) {
				fail("z was the last one");
				break;
			}
		}
	}

	@Test
	public void testBlockingMethodsBasic() throws Exception {
		final WeightedBlockingQueue<String> q = new WeightedBlockingQueue<String>();
		assertEquals(0, q.size());
		q.put("a");
		assertEquals(1, q.size());
		assertEquals("a", q.remove());
		assertEquals(0, q.size());

		final StringBuilder value = new StringBuilder();
		final CountDownLatch latch = new CountDownLatch(2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					value.append(q.take());
					latch.countDown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					q.put("a");
					latch.countDown();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		latch.await();
		assertEquals("a", value.toString());
	}

	@Test
	public void testBlockingMethodsTimedAbort() throws Exception {
		final WeightedBlockingQueue<String> q = new WeightedBlockingQueue<String>();
		assertEquals(0, q.size());
		q.put("a");
		assertEquals(1, q.size());
		assertEquals("a", q.remove());
		assertEquals(0, q.size());

		final StringBuilder value = new StringBuilder();
		final CountDownLatch latch = new CountDownLatch(2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String val = q.poll(100, TimeUnit.MILLISECONDS);
					if (val != null)
						value.append(val);
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					q.put("a");
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		latch.await();
		// this one shouldn't happen yet as timeout is short
		assertEquals("", value.toString());
	}

	@Test
	public void testBlockingMethodsTimed() throws Exception {
		final WeightedBlockingQueue<String> q = new WeightedBlockingQueue<String>();
		assertEquals(0, q.size());
		q.put("a");
		assertEquals(1, q.size());
		assertEquals("a", q.remove());
		assertEquals(0, q.size());

		final StringBuilder value = new StringBuilder();
		final CountDownLatch latch = new CountDownLatch(2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String val = q.poll(10, TimeUnit.SECONDS);
					if (val != null)
						value.append(val);
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					q.put("a");
					latch.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		latch.await();
		assertEquals("a", value.toString());
	}
}
