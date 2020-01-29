package io.rjuelich.learn.jmonkey.vessel;

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


	public void forward() {
		node.move(0,0,1);
	}
	
	public void turnUp() {
		node.rotate(new Quaternion().fromAngleAxis(-ROTATION_DEGREE, Vector3f.UNIT_X));
	}

	public void turnDown() {
		node.rotate(new Quaternion().fromAngleAxis(ROTATION_DEGREE, Vector3f.UNIT_X));
	}
	
	public void turnRight() {
		node.rotate(new Quaternion().fromAngleAxis(-ROTATION_DEGREE, Vector3f.UNIT_Y));
	}

	public void turnRightTo(float newAngle) {
		Quaternion Yroll45 = new Quaternion();
		Yroll45.fromAngleAxis(45 * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
		
		Quaternion localRotation = node.getLocalRotation();
		
		Quaternion quat = new Quaternion();
		quat.slerp(localRotation,Yroll45, .1f);
		
		node.setLocalRotation(quat);
	}
	
	public void turnLeft() {
		node.rotate(new Quaternion().fromAngleAxis(ROTATION_DEGREE, Vector3f.UNIT_Y));
	}
	
	UpdateControl control = new UpdateControl() {
		public void update(float tpf) {
			
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
