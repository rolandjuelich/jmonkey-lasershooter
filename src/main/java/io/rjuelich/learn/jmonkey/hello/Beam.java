package io.rjuelich.learn.jmonkey.hello;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.light.PointLight;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;

public class Beam {

	private static final int TTL_MIN = 0;
	private static final int TTL_MAX = 50;

	private final AssetManager assetManager;
	private final Node rootNode;
	private final Node node;
	private final AudioNode sound;
	private final LightControl lightControl;
	private final PointLight light;
	private final Vector3f direction;

	private final Target target;


	private int duration;

	public Beam(final AssetManager assetManager, final Node rootNode, final Camera camera, final Target target) {
		this.assetManager = assetManager;
		this.rootNode = rootNode;
		this.target = target;

		node = new Node();

		node.attachChild(createModel());
		sound = createSound();
		node.attachChild(sound);

		node.setLocalTranslation(camera.getLocation().add(0, 1, 0));
		node.setLocalRotation(camera.getRotation());
		direction = camera.getDirection().normalize();

		light = new PointLight();
		light.setColor(ColorRGBA.Red);
		light.setRadius(3);
		light.setPosition(node.getLocalTranslation());
		rootNode.addLight(light);

		lightControl = new LightControl(light);
		node.addControl(lightControl);

		duration = TTL_MIN;

	}

	public void update() {
		if (duration == TTL_MIN) {
			rootNode.attachChild(node);
			sound.playInstance();
		}

		duration++;

		node.move(direction);

		final CollisionResults results = new CollisionResults();
		node.collideWith(target.getBoundingVolume(), results);
		if (results.size() > 0) {
			final Vector3f location = results.getClosestCollision().getGeometry().getWorldTranslation();
			duration = TTL_MAX;
			target.hitBy(this, location);
		}

		if (duration == TTL_MAX) {
			node.removeControl(lightControl);
			rootNode.removeLight(light);
			rootNode.detachChild(node);
		}
	}

	private AudioNode createSound() {
		final AudioNode sound = new AudioNode(assetManager, "Sound/Effects/lasergun.wav", DataType.Buffer);
		sound.setPositional(false);
		sound.setLooping(false);
		sound.setVolume(.2f);
		return sound;
	}

	private Spatial createModel() {
		final Spatial model = assetManager.loadModel("Models/beam.blend");
		model.depthFirstTraversal(new SceneGraphVisitorAdapter() {
			@Override
			public void visit(final Geometry geom) {
				geom.getMaterial().setColor("GlowColor", ColorRGBA.Red);
				geom.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.AlphaAdditive);
				geom.getMaterial().getAdditionalRenderState().setDepthWrite(false);
				geom.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
				geom.setQueueBucket(Bucket.Transparent);
			}
		});
		return model;
	}

}
