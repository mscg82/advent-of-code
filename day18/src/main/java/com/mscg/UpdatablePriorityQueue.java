package com.mscg;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class UpdatablePriorityQueue<E> implements UpdatableQueue<E>
{

	private static final int DEFAULT_INITIAL_CAPACITY = 11;

	private static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

	private static int newLength(final int oldLength, final int minGrowth, final int prefGrowth)
	{
		final int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
		if (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
			return prefLength;
		} else {
			// put code cold in a separate method
			return hugeLength(oldLength, minGrowth);
		}
	}

	private static int hugeLength(final int oldLength, final int minGrowth)
	{
		final int minLength = oldLength + minGrowth;
		if (minLength < 0) { // overflow
			throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
		} else {
			return Math.max(minLength, SOFT_MAX_ARRAY_LENGTH);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void siftUpComparable(int k, final T x, final Object[] es)
	{
		final Comparable<? super T> key = (Comparable<? super T>) x;
		while (k > 0) {
			final int parent = (k - 1) >>> 1;
			final Object e = es[parent];
			if (key.compareTo((T) e) >= 0) {
				break;
			}
			es[k] = e;
			k = parent;
		}
		es[k] = key;
	}

	@SuppressWarnings("unchecked")
	private static <T> void siftUpUsingComparator(int k, final T x, final Object[] es, final Comparator<? super T> cmp)
	{
		while (k > 0) {
			final int parent = (k - 1) >>> 1;
			final Object e = es[parent];
			if (cmp.compare(x, (T) e) >= 0) {
				break;
			}
			es[k] = e;
			k = parent;
		}
		es[k] = x;
	}

	@SuppressWarnings("unchecked")
	private static <T> void siftDownComparable(int k, final T x, final Object[] es, final int n)
	{
		final Comparable<? super T> key = (Comparable<? super T>) x;
		final int half = n >>> 1; // loop while a non-leaf
		while (k < half) {
			int child = (k << 1) + 1; // assume left child is least
			Object c = es[child];
			final int right = child + 1;
			if (right < n && ((Comparable<? super T>) c).compareTo((T) es[right]) > 0) {
				child = right;
				c = es[child];
			}
			if (key.compareTo((T) c) <= 0) {
				break;
			}
			es[k] = c;
			k = child;
		}
		es[k] = key;
	}

	@SuppressWarnings("unchecked")
	private static <T> void siftDownUsingComparator(int k, final T x, final Object[] es, final int n,
			final Comparator<? super T> cmp)
	{
		final int half = n >>> 1;
		while (k < half) {
			int child = (k << 1) + 1;
			Object c = es[child];
			final int right = child + 1;
			if (right < n && cmp.compare((T) c, (T) es[right]) > 0) {
				child = right;
				c = es[child];
			}
			if (cmp.compare(x, (T) c) <= 0) {
				break;
			}
			es[k] = c;
			k = child;
		}
		es[k] = x;
	}

	/**
	 * Priority queue represented as a balanced binary heap: the two children of queue[n] are queue[2*n+1] and queue[2*(n+1)]. The
	 * priority queue is ordered by comparator, or by the elements' natural ordering, if comparator is null: For each node n in the
	 * heap and each descendant d of n, n <= d. The element with the lowest value is in queue[0], assuming the queue is nonempty.
	 */
	private Object[] queue; // non-private to simplify nested class access

	/**
	 * The number of elements in the priority queue.
	 */
	private int size;

	/**
	 * The comparator, or null if priority queue uses elements' natural ordering.
	 */
	private final Comparator<? super E> comparator;

	/**
	 * Creates a {@code PriorityQueue} with the default initial capacity (11) that orders its elements according to their
	 * {@linkplain Comparable natural ordering}.
	 */
	public UpdatablePriorityQueue() {
		this(DEFAULT_INITIAL_CAPACITY, null);
	}

	/**
	 * Creates a {@code PriorityQueue} with the specified initial capacity that orders its elements according to their
	 * {@linkplain Comparable natural ordering}.
	 *
	 * @param initialCapacity
	 *            the initial capacity for this priority queue
	 * @throws IllegalArgumentException
	 *             if {@code initialCapacity} is less than 1
	 */
	public UpdatablePriorityQueue(final int initialCapacity) {
		this(initialCapacity, null);
	}

	/**
	 * Creates a {@code PriorityQueue} with the default initial capacity and whose elements are ordered according to the specified
	 * comparator.
	 *
	 * @param comparator
	 *            the comparator that will be used to order this priority queue. If {@code null}, the {@linkplain Comparable natural
	 *            ordering} of the elements will be used.
	 * @since 1.8
	 */
	public UpdatablePriorityQueue(final Comparator<? super E> comparator) {
		this(DEFAULT_INITIAL_CAPACITY, comparator);
	}

	/**
	 * Creates a {@code PriorityQueue} with the specified initial capacity that orders its elements according to the specified
	 * comparator.
	 *
	 * @param initialCapacity
	 *            the initial capacity for this priority queue
	 * @param comparator
	 *            the comparator that will be used to order this priority queue. If {@code null}, the {@linkplain Comparable natural
	 *            ordering} of the elements will be used.
	 * @throws IllegalArgumentException
	 *             if {@code initialCapacity} is less than 1
	 */
	public UpdatablePriorityQueue(final int initialCapacity, final Comparator<? super E> comparator) {
		// Note: This restriction of at least one is not actually needed,
		// but continues for 1.5 compatibility
		if (initialCapacity < 1) {
			throw new IllegalArgumentException();
		}
		this.queue = new Object[initialCapacity];
		this.comparator = comparator;
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}

	@Override
	public int size()
	{
		return size;
	}

	/**
	 * Removes all the elements from this priority queue. The queue will be empty after this call returns.
	 */
	@Override
	public void clear()
	{
		final Object[] es = queue;
		for (int i = 0, n = size; i < n; i++) {
			es[i] = null;
		}
		size = 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E poll()
	{
		final Object[] es = queue;
		final E result = (E) es[0];

		if (result != null) {
			final int n = --size;
			final E x = (E) es[n];
			es[n] = null;
			if (n > 0) {
				final Comparator<? super E> cmp;
				if ((cmp = comparator) == null) {
					siftDownComparable(0, x, es, n);
				} else {
					siftDownUsingComparator(0, x, es, n, cmp);
				}
			}
		}
		return result;
	}

	/**
	 * Inserts the specified element into this priority queue.
	 *
	 * @return {@code true} (as specified by {@link Collection#add})
	 * @throws ClassCastException
	 *             if the specified element cannot be compared with elements currently in this priority queue according to the
	 *             priority queue's ordering
	 * @throws NullPointerException
	 *             if the specified element is null
	 */
	@Override
	public boolean add(final E e)
	{
		return offer(e);
	}

	/**
	 * Inserts the specified element into this priority queue.
	 *
	 * @return {@code true} (as specified by {@link Queue#offer})
	 * @throws ClassCastException
	 *             if the specified element cannot be compared with elements currently in this priority queue according to the
	 *             priority queue's ordering
	 * @throws NullPointerException
	 *             if the specified element is null
	 */
	@Override
	public boolean offer(final E e)
	{
		if (e == null) {
			throw new NullPointerException();
		}
		final int i = size;
		if (i >= queue.length) {
			grow(i + 1);
		}
		siftUp(i, e);
		size = i + 1;
		return true;
	}

	@Override
	public void update()
	{
		heapify();
	}

	/**
	 * Retrieves and removes the head of this queue. This method differs from {@link #poll poll} only in that it throws an exception
	 * if this queue is empty.
	 *
	 * <p>
	 * This implementation returns the result of {@code poll} unless the queue is empty.
	 *
	 * @return the head of this queue
	 * @throws NoSuchElementException
	 *             if this queue is empty
	 */
	@Override public E remove()
	{
		final E x = poll();
		if (x != null) {
			return x;
		} else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * Removes a single instance of the specified element from this queue, if it is present. More formally, removes an element
	 * {@code e} such that {@code o.equals(e)}, if this queue contains one or more such elements. Returns {@code true} if and only
	 * if this queue contained the specified element (or equivalently, if this queue changed as a result of the call).
	 *
	 * @param o
	 *            element to be removed from this queue, if present
	 * @return {@code true} if this queue changed as a result of the call
	 */
	@Override
	public boolean remove(final Object o)
	{
		final int i = indexOf(o);
		if (i == -1) {
			return false;
		} else {
			removeAt(i);
			return true;
		}
	}

	/**
	 * Inserts item x at position k, maintaining heap invariant by demoting x down the tree repeatedly until it is less than or
	 * equal to its children or is a leaf.
	 *
	 * @param k
	 *            the position to fill
	 * @param x
	 *            the item to insert
	 */
	private void siftDown(final int k, final E x)
	{
		if (comparator != null) {
			siftDownUsingComparator(k, x, queue, size, comparator);
		} else {
			siftDownComparable(k, x, queue, size);
		}
	}

	/**
	 * Establishes the heap invariant (described above) in the entire tree, assuming nothing about the order of the elements prior
	 * to the call. This classic algorithm due to Floyd (1964) is known to be O(size).
	 */
	@SuppressWarnings("unchecked")
	private void heapify()
	{
		final Object[] es = queue;
		final int n = size;
		int i = (n >>> 1) - 1;
		final Comparator<? super E> cmp;
		if ((cmp = comparator) == null) {
			for (; i >= 0; i--) {
				siftDownComparable(i, (E) es[i], es, n);
			}
		} else {
			for (; i >= 0; i--) {
				siftDownUsingComparator(i, (E) es[i], es, n, cmp);
			}
		}
	}

	private int indexOf(final Object o)
	{
		if (o != null) {
			final Object[] es = queue;
			for (int i = 0, n = size; i < n; i++) {
				if (o.equals(es[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Inserts item x at position k, maintaining heap invariant by promoting x up the tree until it is greater than or equal to its
	 * parent, or is the root.
	 *
	 * To simplify and speed up coercions and comparisons, the Comparable and Comparator versions are separated into different
	 * methods that are otherwise identical. (Similarly for siftDown.)
	 *
	 * @param k
	 *            the position to fill
	 * @param x
	 *            the item to insert
	 */
	private void siftUp(final int k, final E x)
	{
		if (comparator != null) {
			siftUpUsingComparator(k, x, queue, comparator);
		} else {
			siftUpComparable(k, x, queue);
		}
	}

	/**
	 * Increases the capacity of the array.
	 *
	 * @param minCapacity
	 *            the desired minimum capacity
	 */
	private void grow(final int minCapacity)
	{
		final int oldCapacity = queue.length;
		// Double size if small; else grow by 50%
		final int newCapacity = newLength(oldCapacity, minCapacity - oldCapacity, /* minimum growth */
				oldCapacity < 64 ? oldCapacity + 2 : oldCapacity >> 1
		/* preferred growth */);
		queue = Arrays.copyOf(queue, newCapacity);
	}

	/**
	 * Removes the ith element from queue.
	 *
	 * Normally this method leaves the elements at up to i-1, inclusive, untouched. Under these circumstances, it returns null.
	 * Occasionally, in order to maintain the heap invariant, it must swap a later element of the list with one earlier than i.
	 * Under these circumstances, this method returns the element that was previously at the end of the list and is now at some
	 * position before i. This fact is used by iterator.remove so as to avoid missing traversing elements.
	 */
	@SuppressWarnings("unchecked")
	E removeAt(final int i)
	{
		final Object[] es = queue;
		final int s = --size;
		if (s == i) // removed last element
		{
			es[i] = null;
		} else {
			final E moved = (E) es[s];
			es[s] = null;
			siftDown(i, moved);
			if (es[i] == moved) {
				siftUp(i, moved);
				if (es[i] != moved) {
					return moved;
				}
			}
		}
		return null;
	}

}
