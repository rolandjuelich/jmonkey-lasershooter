package io.rjuelich.learn.jmonkey.vessel;

import static com.jme3.math.FastMath.nextRandomInt;
import static com.jme3.math.Vector3f.UNIT_Y;
import static com.jme3.math.Vector3f.UNIT_Z;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.math.ColorRGBA;
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

	private int speed = 0;

	public Interceptor(final AssetManager assetManager, final Node rootNode) {
		this.assets = assetManager;
		this.node = new Node();

		loadModel(node);
		node.addControl(navigationControl);
		node.addControl(speedControl);
		rootNode.attachChild(node);
	}

	UpdateControl navigationControl = new UpdateControl() {
		public void update(final float tpf) {
			Quaternion currentRotation = node.getWorldRotation();
			Quaternion targetRotation = node.getLocalRotation()
					.lookAt(node.getLocalTranslation().negate().add(destination), UNIT_Y);
			Quaternion newRotation = new Quaternion().slerp(currentRotation, targetRotation, tpf);
			node.setLocalRotation(newRotation);
		};
	};

	UpdateControl speedControl = new UpdateControl() {
		public void update(final float tpf) {
			moveForward();
		};
	};

	private Vector3f destination = Vector3f.ZERO;

	protected void moveForward() {
		node.move(viewDirection().mult(speed).mult(.01f));
	}

	public void setCourseTo(final Vector3f destination) {
		this.destination = destination;
	}

	public Vector3f viewDirection() {
		return node.getWorldRotation().mult(UNIT_Z);
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

	public Vector3f getLocation() {
		return node.getWorldTranslation();
	}

	public void alterCourseRandomly() {
		setCourseTo(new Vector3f(nextRandomInt(), nextRandomInt(), nextRandomInt()));
	}

	public void fullStop() {
		speed = 0;
	}

	public void accelerate() {
		if (speed < 10) {
			speed++;
		}
	}

	public void decelerate() {
		if (speed > 0) {
			speed--;
		}
	}

}
