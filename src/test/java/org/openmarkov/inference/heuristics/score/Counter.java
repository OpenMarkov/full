/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.inference.heuristics.score;

public class Counter {

	private int count;

	public Counter(int initCounter) {
		count = initCounter;
	}

	public synchronized int getCount() {
		return count;
	}

	public synchronized void incrementCount() {
		count++;
	}

}
