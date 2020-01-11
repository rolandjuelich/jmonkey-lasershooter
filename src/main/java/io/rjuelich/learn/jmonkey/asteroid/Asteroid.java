package io.rjuelich.learn.jmonkey.asteroid;

import static com.jme3.math.Vector3f.ZERO;
import static org.apache.commons.lang3.RandomUtils.nextInt;

import java.util.HashSet;
import java.util.Set;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.UpdateControl;
import com.jme3.texture.Texture;

import io.rjuelich.learn.jmonkey.hello.Beam;
import io.rjuelich.learn.jmonkey.hello.Collidable;

public class Asteroid implements Collidable<Beam> {

	private final AssetManager assetManager;
	private final Node node;
	private final Spatial model;
	private final Set<Beam> hits = new HashSet<Beam>();
	private Node rootNode;

	public Asteroid(final AssetManager assetManager, final Node rootNode) {
		this.assetManager = assetManager;
		this.rootNode = rootNode;
		this.model = assetManager.loadModel("Models/asteroid.blend");
		this.model.addControl(rotation);
		this.node = new Node();
		this.node.attachChild(model);
		rootNode.attachChild(this.node);
		
		this.model.setName("asteroid");
	}

	@Override
	public void collidesWith(final Beam beam, final Vector3f location) {
		if (hits.contains(beam)) {
			return;
		}

		if (hits.size() < 10) {
			// take hit
			hits.add(beam);
			
			final AudioNode sound = createExplosionSound();
			node.attachChild(sound);
			sound.setLocalTranslation(location);
			sound.play();

			final ParticleEmitter emitter = createExplosionEmitter();
			node.attachChild(emitter);
			emitter.setLocalTranslation(location);
			emitter.emitAllParticles();

		} else {
			// explode
			model.removeFromParent();
			
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
					if(emitter.getNumVisibleParticles()==0) {
						node.removeFromParent();
						System.out.println(rootNode.hasChild(model));
					};
				}
			});

		}
	}

	@Override
	public BoundingVolume getBoundingVolume() {
		return model.getWorldBound();
	}

	public Spatial getModel() {
		return model;
	}

	UpdateControl rotation = new UpdateControl() {
		@Override
		public void update(final float tpf) {
			model.rotate(0, .001f, 0);
		}
	};

	private ParticleEmitter createExplosionEmitter() {
		final Texture texture = assetManager
				.loadTexture("Effects/Explosions/sinestasiastudio/explosion " + nextInt(1, 5) + ".png");
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

	@Override
	public boolean exists() {
		return this.rootNode.hasChild(this.node);
	}
}
