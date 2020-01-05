package io.rjuelich.learn.jmonkey.hello;

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
import com.jme3.texture.Texture;

public class Target {

	private final AssetManager assetManager;
	private final Node rootNode;
	private final Spatial model;
	private final Set<Beam> hits = new HashSet<Beam>();

	public Target(final AssetManager assetManager, final Node rootNode, final Spatial model) {
		this.assetManager = assetManager;
		this.rootNode = rootNode;
		this.model = model;

		rootNode.attachChild(model);
	}

	public BoundingVolume getBoundingVolume() {
		return model.getWorldBound();
	}

	public void hitBy(final Beam beam, final Vector3f location) {

		if (hits.contains(beam)) {
			return;
		}

		hits.add(beam);

		final AudioNode sound = createExplosionSound();
		rootNode.attachChild(sound);
		sound.setLocalTranslation(location);
		sound.play();

		final ParticleEmitter emitter = createExplosionEmitter();
		rootNode.attachChild(emitter);
		emitter.setLocalTranslation(location);
		emitter.emitAllParticles();

	}

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

	private AudioNode createExplosionSound() {
		final AudioNode sound = new AudioNode(assetManager, "Sound/Effects/explosion.wav", DataType.Buffer);
		sound.setPositional(true);
		sound.setLooping(false);
		sound.setVolume(.05f);
		return sound;
	}

}
