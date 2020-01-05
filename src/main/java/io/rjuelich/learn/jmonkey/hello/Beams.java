package io.rjuelich.learn.jmonkey.hello;

import java.util.ArrayList;
import java.util.List;

public class Beams {

	private final List<Beam> values = new ArrayList<>();

	public Beams add(final Beam beam) {
		values.add(beam);
		return this;
	}

	public void update() {
		for (final Beam beam : values) {
			beam.update();
		}
	}

}
