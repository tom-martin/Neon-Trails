package com.heychinaski.droid.wp.trails.color;

import android.graphics.Color;

public class HueBasedRandomColorGenerator implements ColorGenerator {
	public final static int RED = 0;
	public final static int GREEN = 1;
	public final static int BLUE = 2;
	
	public int[] mainColorIndexes;

	public HueBasedRandomColorGenerator(int ... colors) {
		mainColorIndexes = colors;
	}
	
	@Override
	public int generateColor() {
		int mainColorValue = 10 + (int)(Math.random() * 246);
		
		int secondaryColorValue = (int)(Math.random() * mainColorValue);
		
		int colorValues[] = new int[] {-1, -1, -1};
		
		for(int colorIndex : mainColorIndexes) {
			colorValues[colorIndex] = mainColorValue;
		}
		
		for(int i = 0; i < colorValues.length; i++) {
			if(colorValues[i] == -1) {
				colorValues[i] = secondaryColorValue;
			}
		}
		
		return Color.rgb(colorValues[RED], colorValues[GREEN], colorValues[BLUE]);
	}

}
