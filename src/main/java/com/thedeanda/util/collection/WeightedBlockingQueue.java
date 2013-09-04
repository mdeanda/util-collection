package com.thedeanda.util.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * a blocking queue implementation that allows elemented to carry a weight to
 * help prioritize it when used with a thread pool executor. the queue creates
 * buckets for each weight defined in the constructor and any call to
 * get/poll/element/peek/remove etc. randomly selects the first item from the
 * non-empty buckets to act on. as such, calls to peek/poll may not match
 */
public class WeightedBlockingQueue<T> implements BlockingQueue<T> {
	public static final int DEFAULT_WEIGHT = 1;
	public static final int DEFAULT_MAX_WEIGHT = 10;
	public static final int MIN_WEIGHT = 1;

	private Random random;
	private List<List<T>> buckets;
	private ReentrantLock lock;
	private Condition notEmpty;

	public WeightedBlockingQueue() {
		this(DEFAULT_MAX_WEIGHT);
	}

	public WeightedBlockingQueue(int maxWeight) {
		random = new Random();
		lock = new ReentrantLock();
		notEmpty = lock.newCondition();

		lock.lock();
		try {
			if (maxWeight < MIN_WEIGHT) {
				throw new IllegalArgumentException(
						"maxWeight is lower than MIN_WEIGHT");
			}

			buckets = new ArrayList<List<T>>(maxWeight);
			for (int i = MIN_WEIGHT; i <= maxWeight; i++) {
				buckets.add(new LinkedList<T>());
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean add(T e) {
		return add(e, DEFAULT_WEIGHT);
	}

	public boolean add(T element, int weight) {
		if (element == null) {
			throw new NullPointerException();
		}
		if (weight < MIN_WEIGHT) {
			throw new IllegalArgumentException(
					"weight is lower than MIN_WEIGHT");
		}
		weight--;
		if (weight > buckets.size()) {
			throw new IllegalArgumentException("weight is higher max weight");
		}

		lock.lock();
		try {
			buckets.get(weight).add(element);
			notEmpty.signal();
		} finally {
			lock.unlock();
		}

		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		if (c == null) {
			throw new NullPointerException();
		}
		if (!c.isEmpty()) {
			for (T t : c) {
				add(t);
			}
		}
		return true;
	}

	@Override
	public void clear() {
		lock.lock();
		try {
			for (List<T> bucket : buckets) {
				bucket.clear();
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean contains(Object o) {
		lock.lock();
		try {
			for (List<T> bucket : buckets) {
				if (bucket.contains(o)) {
					return true;
				}
			}
		} finally {
			lock.unlock();
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		lock.lock();
		try {
			for (Object t : c) {
				boolean has = false;

				for (List<T> bucket : buckets) {
					if (bucket.contains(t)) {
						has = true;
						break;
					}
				}

				if (!has) {
					return false;
				}
			}
		} finally {
			lock.unlock();
		}
		return true;
	}

	@Override
	public int drainTo(Collection<? super T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int drainTo(Collection<? super T> c, int maxElements) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T element() {
		try {
			return getNext(false, true, false, 0, null);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * all the get/poll/remove/etc methods are the same with minor differences,
	 * consolidate differences here
	 * 
	 * @param remove
	 * @param exceptionOnEmpty
	 * @return
	 * @throws InterruptedException
	 */
	private T getNext(boolean remove, boolean exceptionOnEmpty, boolean wait,
			long timeout, TimeUnit unit) throws InterruptedException {
		// TODO implemented method stub
		T ret = null;
		lock.lock();
		try {
			if (wait) {
				// TODO: timeunit version?
				if (timeout > 0) {
					notEmpty.await(timeout, unit);
				} else {
					notEmpty.await();
				}
			}

			// do non-blocking versions

			if (isEmpty()) {
				if (exceptionOnEmpty) {
					throw new NoSuchElementException();
				} else {
					ret = null;
				}
			} else {
				int weights = getNextBucketsWeighted();
				int useItem = random.nextInt(weights);
				int bucketIndex = 0;
				while (useItem > 0) {
					do {
						bucketIndex++;
					} while (bucketIndex < buckets.size()
							&& buckets.get(bucketIndex).isEmpty());
					useItem -= (bucketIndex + 1);
				}
				List<T> bucket = buckets.get(bucketIndex);
				if (exceptionOnEmpty && bucket.isEmpty()) {
					throw new NoSuchElementException();
				} else if (bucket.isEmpty()) {
					ret = null;
				} else {
					ret = bucket.get(0);
					if (remove) {
						bucket.remove(0);
					}
				}
			}
		} finally {
			lock.unlock();
		}

		return ret;
	}

	/** returns sum of weights of non-empty buckets to use for random retrieval */
	private int getNextBucketsWeighted() {
		int ret = 0;
		int index = 1;
		for (List<T> bucket : buckets) {
			if (!bucket.isEmpty()) {
				ret += index;
			}
			index++;
		}
		return ret;
	}

	@Override
	public boolean isEmpty() {
		lock.lock();
		try {
			boolean ret = true;
			for (List<T> bucket : buckets) {
				if (!bucket.isEmpty()) {
					ret = false;
					break;
				}
			}
			return ret;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Iterator<T> iterator() {
		lock.lock();
		try {
			List<T> tmp = new ArrayList<T>(size());
			for (List<T> bucket : buckets) {
				tmp.addAll(bucket);
			}
			return tmp.iterator();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean offer(T e) {
		return add(e, DEFAULT_WEIGHT);
	}

	@Override
	public boolean offer(T e, long timeout, TimeUnit unit)
			throws InterruptedException {
		return add(e, DEFAULT_WEIGHT);
	}

	@Override
	public T peek() {
		try {
			return getNext(false, false, false, 0, null);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public T poll() {
		try {
			return getNext(true, false, false, 0, null);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public T poll(long timeout, TimeUnit unit) throws InterruptedException {
		return this.getNext(true, false, true, timeout, unit);
	}

	@Override
	public void put(T e) throws InterruptedException {
		add(e, DEFAULT_WEIGHT);
	}

	@Override
	public int remainingCapacity() {
		return Integer.MAX_VALUE;
	}

	@Override
	public T remove() {
		try {
			return getNext(true, true, false, 0, null);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean ret = false;
		lock.lock();
		try {
			for (Object o : c) {
				if (remove(o) && !ret) {
					ret = true;
				}
			}
		} finally {
			lock.unlock();
		}
		return ret;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		lock.lock();
		try {
			int size = 0;
			for (List<T> bucket : buckets) {
				size += bucket.size();
			}
			return size;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public T take() throws InterruptedException {
		return getNext(true, false, true, 0, null);
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

}
