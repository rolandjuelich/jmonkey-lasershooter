package io.rjuelich.learn.jmonkey;

import static com.jme3.math.ColorRGBA.Blue;
import static com.jme3.math.ColorRGBA.Green;
import static com.jme3.math.ColorRGBA.Red;
import static com.jme3.math.Vector3f.UNIT_X;
import static com.jme3.math.Vector3f.UNIT_Y;
import static com.jme3.math.Vector3f.UNIT_Z;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;

public class DebugNode extends Node {

	private final AssetManager assetManager;

	public DebugNode(final AssetManager assetManager) {
		this.assetManager = assetManager;
		attachChild(axis(UNIT_X.mult(5), color(Red), "x-axis"));
		attachChild(axis(UNIT_Y.mult(5), color(Green), "y-axis"));
		attachChild(axis(UNIT_Z.mult(5), color(Blue), "z-axis"));

	}

	private Geometry axis(final Vector3f axis, final Material color, final String name) {
		final Geometry x = new Geometry(name, new Arrow(axis));
		x.setMaterial(color);
		return x;
	}

	private Material color(final ColorRGBA color) {
		final Material material = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		material.setColor("Color", color);
		return material;
	}
}
