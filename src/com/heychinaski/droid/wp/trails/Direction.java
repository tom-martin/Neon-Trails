package com.heychinaski.droid.wp.trails;

import android.graphics.Point;

/**
 * Represents four directions, up, down, left and right.
 * @author tomm
 *
 */
public enum Direction {
	UP {
		private Point up = new Point(0, -1);
		@Override
		public Point getDirectionVector() {
			return up;
		}
		
		@Override
		public Direction getOppositeDirection() {
			return DOWN;
		}
	},
	DOWN  {
		private Point down = new Point(0, 1);
		@Override
		public Point getDirectionVector() {
			return down;
		}
		
		@Override
		public Direction getOppositeDirection() {
			return UP;
		}
	},
	LEFT  {
		private Point left = new Point(-1, 0);
		@Override
		public Point getDirectionVector() {
			return left;
		}
		
		@Override
		public Direction getOppositeDirection() {
			return RIGHT;
		}
	},
	RIGHT  {
		private Point right = new Point(1, 0);
		
		@Override
		public Point getDirectionVector() {
			return right;
		}
		
		@Override
		public Direction getOppositeDirection() {
			return LEFT;
		}
	};
	
	/**
	 * The vector that should be applied to translate a point
	 * one unit in the given direction.
	 * E.g UP direction vector is (0, -1)
	 * @return
	 */
	public abstract Point getDirectionVector();
	
	/**
	 * The opposite direction, e.g the opposite of UP is DOWN
	 * @return
	 */
	public abstract Direction getOppositeDirection();
	
	public void applyDirectionToPoint(Point toMove) {
		Point directionVector = getDirectionVector();
		toMove.x += directionVector.x;
		toMove.y += directionVector.y;
	}
}
