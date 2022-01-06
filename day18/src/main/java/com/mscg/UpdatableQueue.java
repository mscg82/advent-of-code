package com.mscg;

import java.util.Collection;
import java.util.NoSuchElementException;

public interface UpdatableQueue<E>
{

	/**
	 * Returns the number of elements in this collection. If this collection contains more than {@code Integer.MAX_VALUE} elements,
	 * returns {@code Integer.MAX_VALUE}.
	 *
	 * @return the number of elements in this collection
	 */
	int size();

	/**
	 * Removes all the elements from this collection (optional operation). The collection will be empty after this method returns.
	 *
	 * @throws UnsupportedOperationException
	 *             if the {@code clear} operation is not supported by this collection
	 */
	void clear();

	/**
	 * Retrieves and removes the head of this queue, or returns {@code null} if this queue is empty.
	 *
	 * @return the head of this queue, or {@code null} if this queue is empty
	 */
	E poll();

	/**
	 * Returns {@code true} if this collection contains no elements.
	 *
	 * @return {@code true} if this collection contains no elements
	 */
	boolean isEmpty();

	/**
	 * Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions,
	 * returning {@code true} upon success and throwing an {@code IllegalStateException} if no space is currently available.
	 *
	 * @param e
	 *            the element to add
	 * @return {@code true} (as specified by {@link Collection#add})
	 * @throws IllegalStateException
	 *             if the element cannot be added at this time due to capacity restrictions
	 * @throws ClassCastException
	 *             if the class of the specified element prevents it from being added to this queue
	 * @throws NullPointerException
	 *             if the specified element is null and this queue does not permit null elements
	 * @throws IllegalArgumentException
	 *             if some property of this element prevents it from being added to this queue
	 */
	boolean add(E e);

	/**
	 * Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions.
	 * When using a capacity-restricted queue, this method is generally preferable to {@link #add}, which can fail to insert an
	 * element only by throwing an exception.
	 *
	 * @param e
	 *            the element to add
	 * @return {@code true} if the element was added to this queue, else {@code false}
	 * @throws ClassCastException
	 *             if the class of the specified element prevents it from being added to this queue
	 * @throws NullPointerException
	 *             if the specified element is null and this queue does not permit null elements
	 * @throws IllegalArgumentException
	 *             if some property of this element prevents it from being added to this queue
	 */
	boolean offer(E e);

	/**
	 * This method will reorganize the elements in the queue in case they havebeen updated.
	 */
	void update();

	/**
	 * Retrieves and removes the head of this queue. This method differs from {@link #poll() poll()} only in that it throws an
	 * exception if this queue is empty.
	 *
	 * @return the head of this queue
	 * @throws NoSuchElementException
	 *             if this queue is empty
	 */
	E remove();

	/**
	 * Removes a single instance of the specified element from this queue, if it is present. More formally, removes an element
	 * {@code e} such that {@code o.equals(e)}, if this queue contains one or more such elements. Returns {@code true} if and only
	 * if this queue contained the specified element (or equivalently, if this queue changed as a result of the call).
	 *
	 * @param o
	 *            element to be removed from this queue, if present
	 * @return {@code true} if this queue changed as a result of the call
	 */
	boolean remove(Object o);
}
