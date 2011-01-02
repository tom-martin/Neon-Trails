package com.heychinaski.droid.wp.trails;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

/**
 * Represents one trail.  Some members are only present to avoid
 * unnecessary memory allocation (and therefore garbage collection)
 * as I'm working on the assumption that garbage collection is expensive.
 * 
 * @author tomm
 *
 */
public class Trail {

	private Point currentPoint = new Point();
	
	/** Only used in one method, but kept around so we don't have to keep gcing*/
	private Point destinationPoint = new Point();

	private Grid grid;

	private boolean terminated = false;

	private Direction currentDirection;

	private List<Point> tail = new ArrayList<Point>();

	/** Also only used in one method */
	private DirectionList availableDirections = new DirectionList();
	
	private long lastAdvanceTime = 0;

	public Trail(Grid grid) {
		super();
		this.grid = grid;

		reset();
	}

	public void reset() {
		removeFromGrid();

		tail.clear();

		currentPoint.x = (int)(Math.random() * grid.getWidth());
		currentPoint.y = (int)(Math.random() * grid.getHeight());
		
		terminated = !grid.isPointAvailable(currentPoint);
	}

	public void advance() {
		if(terminated) {
			return;
		}

		availableDirections.reset();
		for(Direction direction : Direction.values()) {
			if(currentDirection == null || direction != currentDirection.getOppositeDirection()) {
				destinationPoint.set(currentPoint.x, currentPoint.y);
				direction.applyDirectionToPoint(destinationPoint);

				if(grid.isPointAvailable(destinationPoint)) {
					availableDirections.add(direction);
				}
			}
		}

		if(availableDirections.getLength() > 0) {
			// Make sure we add the starting point.
			if(tail.size() == 0) {
				grid.set(currentPoint);
				tail.add(new Point(currentPoint));
			}
			
			currentDirection = pickRandomDirection(availableDirections);
			currentDirection.applyDirectionToPoint(currentPoint);

			grid.set(currentPoint);
			tail.add(new Point(currentPoint));
		} else {
			terminated = true;
		}
	}

	private Direction pickRandomDirection(DirectionList availableDirections) {
		int randomIndex = (int)(Math.random() * availableDirections.getLength());
		return availableDirections.get(randomIndex);
	}

	public boolean isTerminated() {
		return terminated;
	}

	public List<Point> getTail() {
		return tail;
	}

	public void removeFromGrid() {
		if(tail != null) {
			for(int i = 0; i < tail.size(); i++) {
				grid.clear(tail.get(i));
			}
		}
	}

	public long getLastAdvanceTime() {
		return lastAdvanceTime;
	}

	public void setLastAdvanceTime(long lastAdvancetTime) {
		this.lastAdvanceTime = lastAdvancetTime;
	}
}
