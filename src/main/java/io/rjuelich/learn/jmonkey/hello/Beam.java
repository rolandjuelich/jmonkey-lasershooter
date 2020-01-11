package io.rjuelich.learn.jmonkey.hello;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.light.Light;
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
import com.jme3.scene.control.UpdateControl;

public class Beam {

	private static final int TTL_MIN = 0;
	private static final int TTL_MAX = 50;

	private final Node node;
	private final Light light;
	private final Vector3f direction;

	private int duration;
	private Collidable<Beam> target;
	private Node rootNode;

	public Beam(final AssetManager assetManager, final Node rootNode, final Camera camera,
			final Collidable<Beam> target) {
		this.rootNode = rootNode;
		this.target = target;
		this.direction = camera.getDirection().normalize();
		this.duration = TTL_MIN;

		node = new Node();

		node.attachChild(createModel(assetManager));
		node.attachChild(createSound(assetManager));

		node.setLocalTranslation(camera.getLocation().add(0, 1, 0));
		node.setLocalRotation(camera.getRotation());

		light = createLight(rootNode);
		node.addControl(new LightControl(light));

		node.addControl(increaseDuration);
		node.addControl(moveForward);
		node.addControl(checkCollisions);
		node.addControl(termination);

		rootNode.attachChild(node);
	}

	UpdateControl increaseDuration = new UpdateControl() {
		@Override
		public void update(float tpf) {
			duration++;
		}
	};

	UpdateControl moveForward = new UpdateControl() {
		@Override
		public void update(final float tpf) {
			node.move(direction.mult(2));
		}
	};

	UpdateControl checkCollisions = new UpdateControl() {
		@Override
		public void update(float tpf) {
			final CollisionResults results = new CollisionResults();
			if(target.exists()) {
				node.collideWith(target.getBoundingVolume(), results);
				if (results.size() > 0) {
					final Vector3f location = results.getClosestCollision().getGeometry().getWorldTranslation();
					duration = TTL_MAX;
					target.collidesWith(Beam.this, location);
				}
			}
		}
	};

	UpdateControl termination = new UpdateControl() {
		@Override
		public void update(float tpf) {
			if (duration == TTL_MAX) {
				rootNode.removeLight(light);
				rootNode.detachChild(node);
			}
		}
	};

	private Light createLight(final Node rootNode) {
		final PointLight light = new PointLight();
		light.setColor(ColorRGBA.Red);
		light.setRadius(3);
		rootNode.addLight(light); // light must be attached to rootNode to be visible
		return light;
	}

	private AudioNode createSound(AssetManager assetManager) {
		final AudioNode sound = new AudioNode(assetManager, "Sound/Effects/lasergun.wav", DataType.Buffer);
		sound.setPositional(false);
		sound.setLooping(false);
		sound.setVolume(.2f);
		sound.addControl(new UpdateControl() {
			@Override
			public void update(float tpf) {
				if (duration == TTL_MIN) {
					sound.playInstance();
				}
			}
		});

		return sound;
	}

	private Spatial createModel(AssetManager assetManager) {
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
