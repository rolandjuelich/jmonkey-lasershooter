package io.rjuelich.learn.jmonkey.hello;

import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;

public interface Collidable<C> {

	void collidesWith(C collidable, Vector3f location);

	BoundingVolume getBoundingVolume();

	boolean exists();

}