package com.heychinaski.droid.wp.trails;

import com.heychinaski.droid.wp.trails.color.ColorGenerator;
import com.heychinaski.droid.wp.trails.color.HueBasedRandomColorGenerator;
import com.heychinaski.droid.wp.trails.color.RandomColorGenerator;

public enum ColorScheme {
  ANY(new RandomColorGenerator()),
  RED(new HueBasedRandomColorGenerator(HueBasedRandomColorGenerator.RED)),
  GREEN(new HueBasedRandomColorGenerator(HueBasedRandomColorGenerator.GREEN)),
  BLUE(new HueBasedRandomColorGenerator(HueBasedRandomColorGenerator.BLUE)),
  YELLOW(new HueBasedRandomColorGenerator(HueBasedRandomColorGenerator.RED, HueBasedRandomColorGenerator.GREEN)),
  LIGHT_BLUE(new HueBasedRandomColorGenerator(HueBasedRandomColorGenerator.GREEN, HueBasedRandomColorGenerator.BLUE)),
  PINK(new HueBasedRandomColorGenerator(HueBasedRandomColorGenerator.RED, HueBasedRandomColorGenerator.BLUE)),
  BLACK_AND_WHITE(new HueBasedRandomColorGenerator());

  private ColorGenerator colorGenerator;
  
  private ColorScheme(ColorGenerator colorGenerator) {
  	this.colorGenerator = colorGenerator;
	}
  
  public ColorGenerator getColorGenerator() {
  	return colorGenerator;
  }
  
}
