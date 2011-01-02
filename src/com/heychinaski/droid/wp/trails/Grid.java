package com.heychinaski.droid.wp.trails;

import java.util.HashSet;
import java.util.Set;

import android.graphics.Point;

public class Grid {
	private int width;
	private int height;
	
	private Set<Point> setPoints = new HashSet<Point>();

	public Grid(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}
	
	public void set(Point point) {
		setPoints.add(new Point(point));
	}
	
	public void clear(Point point) {
		setPoints.remove(point);
	}
	
	public void clearAll(Point point) {
		setPoints.clear();
	}
	
	public boolean isPointAvailable(Point point) {
		if(setPoints.contains(point)) {
			return false;
		} else {
			return point.x >= 0 && point.y >= 0 && point.x < width && point.y < height;
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
