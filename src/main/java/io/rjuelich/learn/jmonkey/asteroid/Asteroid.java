package io.rjuelich.learn.jmonkey.asteroid;

import static com.jme3.math.Vector3f.ZERO;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.UpdateControl;
import com.jme3.texture.Texture;

public class Asteroid {

	private static final int MAX_HIT_COUNT = 30;
	private final AssetManager assetManager;
	private final Node node;
	private final Spatial model;
	private PhysicsSpace physicsSpace;
	private int hitCount = 0;

	public Asteroid(final AssetManager assetManager, final Node rootNode, PhysicsSpace physicsSpace) {
		this.assetManager = assetManager;
		this.physicsSpace = physicsSpace;
		this.model = assetManager.loadModel("Models/asteroid.blend");
		this.model.addControl(rotation);

		physicsControl = new RigidBodyControl(CollisionShapeFactory.createDynamicMeshShape(model), 0);
		this.model.addControl(physicsControl);
		this.physicsSpace.add(physicsControl);

		this.node = new Node();
		this.node.attachChild(model);
		rootNode.attachChild(model);
		rootNode.attachChild(this.node);

		this.model.setName("asteroid");

		this.physicsSpace.addCollisionListener(new PhysicsCollisionListener() {

			@Override
			public void collision(PhysicsCollisionEvent event) {
				if (hitCount < MAX_HIT_COUNT) {
					sufferDamage(event.getLocalPointA());
				} else {
					explode(event.getLocalPointA());
				}
			}
		});
	}

	private void explode(final Vector3f location) {
		model.removeFromParent();
		physicsSpace.remove(physicsControl);

		final AudioNode sound = createDestructionSound();
		node.attachChild(sound);
		sound.setLocalTranslation(location);
		sound.play();

		final ParticleEmitter emitter = createDestructionEmitter();
		node.attachChild(emitter);
		emitter.setLocalTranslation(location);
		emitter.emitAllParticles();

		emitter.addControl(new UpdateControl() {

			@Override
			public void update(float tpf) {
				if (emitter.getNumVisibleParticles() == 0) {
					node.removeFromParent();
				}
				;
			}
		});
	}

	private void sufferDamage(final Vector3f location) {
		hitCount++;

		final AudioNode sound = createExplosionSound();
		node.attachChild(sound);
		sound.setLocalTranslation(location);
		sound.play();

		final ParticleEmitter emitter = createExplosionEmitter();
		node.attachChild(emitter);
		emitter.setLocalTranslation(location);
		emitter.emitAllParticles();
	}

	UpdateControl rotation = new UpdateControl() {
		@Override
		public void update(final float tpf) {
			model.rotate(0, .001f, 0);
		}
	};
	private RigidBodyControl physicsControl;

	private ParticleEmitter createExplosionEmitter() {
		final Texture texture = assetManager.loadTexture("Effects/Explosions/sinestasiastudio/explosion 2.png");
		final Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		material.setTexture("Texture", texture);

		final ParticleEmitter emitter = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
		emitter.setMaterial(material);
		emitter.setNumParticles(4);
		emitter.setParticlesPerSec(0);
		emitter.setImagesX(8);
		emitter.setImagesY(8);
		emitter.setEndColor(ColorRGBA.White);
		emitter.setStartColor(ColorRGBA.White);
		emitter.setStartSize(.1f);
		emitter.setEndSize(.5f);
		emitter.setRandomAngle(true);

		emitter.setHighLife(.5f);
		emitter.setLowLife(.05f);

		return emitter;
	}

	private ParticleEmitter createDestructionEmitter() {
		final Texture texture = assetManager.loadTexture("Effects/Explosions/ashishlko11/Explosion.png");
		final Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		material.setTexture("Texture", texture);

		final ParticleEmitter emitter = new ParticleEmitter("AsteroidDestruction", ParticleMesh.Type.Triangle, 5);
		emitter.setMaterial(material);
		emitter.setParticlesPerSec(0);
		emitter.setImagesX(4);
		emitter.setImagesY(3);
		emitter.setEndColor(ColorRGBA.White);
		emitter.setStartColor(ColorRGBA.White);
		emitter.setStartSize(.1f);
		emitter.setEndSize(2.5f);
		emitter.setRandomAngle(true);
		emitter.setRotateSpeed(.1f);
		emitter.setGravity(ZERO);
		emitter.setHighLife(.1f);
		emitter.setLowLife(1.5f);

		return emitter;
	}

	private AudioNode createExplosionSound() {
		final AudioNode sound = new AudioNode(assetManager, "Sound/Effects/explosion.wav", DataType.Buffer);
		sound.setPositional(true);
		sound.setLooping(false);
		sound.setVolume(.05f);
		return sound;
	}

	private AudioNode createDestructionSound() {
		final AudioNode sound = new AudioNode(assetManager, "Sound/Effects/rock_breaking.ogg", DataType.Buffer);
		sound.setPositional(true);
		sound.setLooping(false);
		return sound;
	}

}
