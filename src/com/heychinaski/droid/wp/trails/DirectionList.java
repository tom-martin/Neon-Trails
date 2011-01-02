package com.heychinaski.droid.wp.trails;

/**
 * Because we know that we only have four directions we know that (provided
 * all entries are unique) we only need an array of size four so we can avoid
 * unnecessary memory allocation (and therefore unnecessary garbage collection)
 * @author tomm
 *
 */
public class DirectionList {
	public Direction[] directions = new Direction[4];
	
	private int length = 0;
	
	public DirectionList() {
		reset();
	}

	public void reset() {
		length = 0;
		for(int i = 0; i < Direction.values().length; i++) {
			directions[i] = null;
		}
	}
	
	public void add(Direction direction) {
		directions[length] = direction;
		
		length ++;
	}
	
	public Direction get(int i) {
		return directions[i];
	}

	public int getLength() {
		return length;
	}
}
