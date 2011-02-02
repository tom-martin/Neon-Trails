package com.heychinaski.droid.wp.trails;

import com.heychinaski.droid.wp.trails.color.ColorGenerator;
import com.heychinaski.droid.wp.trails.color.HueBasedRandomColorGenerator;
import com.heychinaski.droid.wp.trails.color.RandomColorGenerator;

public enum ColorScheme {
	
  ANY(new RandomColorGenerator(TrailsDrawingThread.LOWEST_COLOR_VALUE)),
  RED(new HueBasedRandomColorGenerator(TrailsDrawingThread.LOWEST_COLOR_VALUE, HueBasedRandomColorGenerator.RED)),
  GREEN(new HueBasedRandomColorGenerator(TrailsDrawingThread.LOWEST_COLOR_VALUE, HueBasedRandomColorGenerator.GREEN)),
  BLUE(new HueBasedRandomColorGenerator(TrailsDrawingThread.LOWEST_COLOR_VALUE, HueBasedRandomColorGenerator.BLUE)),
  YELLOW(new HueBasedRandomColorGenerator(TrailsDrawingThread.LOWEST_COLOR_VALUE, HueBasedRandomColorGenerator.RED, HueBasedRandomColorGenerator.GREEN)),
  LIGHT_BLUE(new HueBasedRandomColorGenerator(TrailsDrawingThread.LOWEST_COLOR_VALUE, HueBasedRandomColorGenerator.GREEN, HueBasedRandomColorGenerator.BLUE)),
  PINK(new HueBasedRandomColorGenerator(TrailsDrawingThread.LOWEST_COLOR_VALUE, HueBasedRandomColorGenerator.RED, HueBasedRandomColorGenerator.BLUE)),
  BLACK_AND_WHITE(new HueBasedRandomColorGenerator(TrailsDrawingThread.LOWEST_COLOR_VALUE));

	private ColorGenerator colorGenerator;
  
  private ColorScheme(ColorGenerator colorGenerator) {
  	this.colorGenerator = colorGenerator;
	}
  
  public ColorGenerator getColorGenerator() {
  	return colorGenerator;
  }
  
}
