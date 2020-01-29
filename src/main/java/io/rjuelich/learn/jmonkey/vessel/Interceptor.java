package io.rjuelich.learn.jmonkey.vessel;

import static com.jme3.math.Vector3f.UNIT_Z;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.UpdateControl;

public class Interceptor {

	private static final float ROTATION_DEGREE = .01f;
	private final AssetManager assets;
	private final Node node;

	public Interceptor(final AssetManager assetManager, final Node rootNode) {
		this.assets = assetManager;
		this.node = new Node();

		loadModel(node);
		node.addControl(control);
		rootNode.attachChild(node);
	}

	public void moveForward() {
		node.move(node.getWorldRotation().mult(UNIT_Z).mult(.1f));
	}

	public void pitchUp() {
		node.rotate(new Quaternion().fromAngleAxis(-ROTATION_DEGREE, Vector3f.UNIT_X));
	}

	public void pitchDown() {
		node.rotate(new Quaternion().fromAngleAxis(ROTATION_DEGREE, Vector3f.UNIT_X));
	}

	public void yawRight() {
		node.rotate(new Quaternion().fromAngleAxis(-ROTATION_DEGREE, Vector3f.UNIT_Y));
	}

	public void yawLeft() {
		node.rotate(new Quaternion().fromAngleAxis(ROTATION_DEGREE, Vector3f.UNIT_Y));
	}

	public void rollLeft() {
		node.rotate(new Quaternion().fromAngleAxis(-ROTATION_DEGREE, Vector3f.UNIT_Z));
	}

	public void rollRight() {
		node.rotate(new Quaternion().fromAngleAxis(ROTATION_DEGREE, Vector3f.UNIT_Z));
	}

	public void turnRightTo(float newAngle) {
		Quaternion Yroll45 = new Quaternion();
		Yroll45.fromAngleAxis(45 * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);

		Quaternion localRotation = node.getLocalRotation();

		Quaternion quat = new Quaternion();
		quat.slerp(localRotation, Yroll45, .1f);

		node.setLocalRotation(quat);
	}

	UpdateControl control = new UpdateControl() {
		public void update(float tpf) {
			// reset rotation around z axis if not aligned
		};
	};

	private void loadModel(final Node node) {
		final Spatial model = assets.loadModel("Models/raider/prototype.blend");
		node.attachChild(model);
		node.depthFirstTraversal(new SceneGraphVisitorAdapter() {
			@Override
			public void visit(final Geometry geom) {
				if (geom.getMaterial() != null && geom.getMaterial().getName() != null
						&& geom.getMaterial().getName().contains("ThrusterPlasma")) {
					final MatParam param = geom.getMaterial().getParam("Color");
					final ColorRGBA color = (ColorRGBA) param.getValue();
					geom.getMaterial().setColor("GlowColor", color);
				}
			}
		});
	}

}
