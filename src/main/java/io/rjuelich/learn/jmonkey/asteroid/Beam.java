package io.rjuelich.learn.jmonkey.asteroid;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.SimplexCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.control.UpdateControl;

public class Beam {

	private static final int TTL_MIN = 0;
	private static final int TTL_MAX = 50;

	private int duration;
	private final Vector3f direction;

	private final Node rootNode;
	private final PhysicsSpace physicsSpace;

	private final Spatial model;
	private final AudioNode sound;
	private final Light light;
	private final GhostControl physicsControl;

	public Beam(final AssetManager assetManager, final Node rootNode, final Vector3f location,
			final Quaternion rotation, final Vector3f direction, final PhysicsSpace physicsSpace) {
		this.rootNode = rootNode;
		this.direction = direction;
		this.physicsSpace = physicsSpace;
		this.duration = TTL_MIN;

		this.physicsControl = new GhostControl(new SimplexCollisionShape(Vector3f.ZERO));
		this.physicsSpace.add(physicsControl);

		this.model = createModel(assetManager, location, rotation, physicsControl);
		this.sound = createSound(assetManager);
		this.light = createLight(rootNode);

		this.rootNode.attachChild(model);
		this.rootNode.attachChild(sound);
		this.rootNode.addLight(light);
		this.rootNode.addControl(new LightControl(light));
		this.rootNode.addControl(new BeamControl());

		this.physicsSpace.addCollisionListener(new PhysicsCollisionListener() {

			@Override
			public void collision(final PhysicsCollisionEvent event) {
				terminate();
			}
		});
		this.sound.addControl(new UpdateControl() {
			@Override
			public void update(final float tpf) {
				if (duration == TTL_MIN + 1) {
					sound.playInstance();
				}
			}
		});
	}

	public class BeamControl extends UpdateControl implements Control {

		@Override
		public void update(final float tpf) {

			duration++;

			move();

			if (duration == TTL_MAX) {
				terminate();
			}
		}

	}

	public void move() {
		model.move(direction);
	}

	public void terminate() {
		rootNode.removeLight(light);
		rootNode.detachChild(model);
		rootNode.detachChild(sound);
		physicsSpace.remove(physicsControl);
	}

	private static Light createLight(final Node rootNode) {
		final PointLight light = new PointLight();
		light.setColor(ColorRGBA.Red);
		light.setRadius(3);
		return light;
	}

	private static AudioNode createSound(final AssetManager assetManager) {
		final AudioNode sound = new AudioNode(assetManager, "Sound/Effects/lasergun.wav", DataType.Buffer);
		sound.setPositional(false);
		sound.setLooping(false);
		sound.setVolume(.2f);
		return sound;
	}

	private static Spatial createModel(final AssetManager assetManager, final Vector3f location,
			final Quaternion rotation, final GhostControl physicsControl) {
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
		model.setLocalTranslation(location);
		model.setLocalRotation(rotation);
		model.addControl(physicsControl);
		return model;
	}

}
