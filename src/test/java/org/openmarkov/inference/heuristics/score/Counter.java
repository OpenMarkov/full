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
