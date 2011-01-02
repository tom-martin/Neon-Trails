package com.heychinaski.droid.wp.trails.color;

import android.graphics.Color;

public class RandomColorGenerator implements ColorGenerator {
	@Override
	public int generateColor() {
		return Color.rgb(10 + (int)(Math.random() * 246), 10 + (int)(Math.random() * 246), 10 + (int)(Math.random() * 246));
	}
}
