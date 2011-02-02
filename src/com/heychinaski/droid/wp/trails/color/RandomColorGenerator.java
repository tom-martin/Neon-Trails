package com.heychinaski.droid.wp.trails.color;

import android.graphics.Color;

public class RandomColorGenerator implements ColorGenerator {
	
	private final int lowestColorValue;
	
	public RandomColorGenerator(int lowestColorValue) {
		super();
		this.lowestColorValue = lowestColorValue;
	}
	@Override
	public int generateColor() {
		int maxColor = 256 - lowestColorValue;
		return Color.rgb(lowestColorValue + (int)(Math.random() * maxColor), lowestColorValue + (int)(Math.random() * maxColor), lowestColorValue + (int)(Math.random() * maxColor));
	}
}
