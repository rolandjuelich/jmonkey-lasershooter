package io.rjuelich.learn.jmonkey.asteroid;

import static com.jme3.math.Vector3f.ZERO;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.control.UpdateControl;
import com.jme3.texture.Texture;

public class ExplosionBlast {

	private final AssetManager assets;
	private final Node root;

	private final ParticleEmitter emitter;
	private final AudioNode sound;

	public ExplosionBlast(final AssetManager assetManager, final Node rootNode) {
		this.assets = assetManager;
		this.root = rootNode;
		
		this.emitter = createEmitter();
		this.sound = createSound();

		this.emitter.addControl(terminationControl);
	}

	public void at(final Vector3f location) {
		emitter.setLocalTranslation(location);
		sound.setLocalTranslation(location);

		root.attachChild(emitter);
		root.attachChild(sound);

		emitter.emitAllParticles();
		sound.play();
	}

	public void terminate() {
		root.detachChild(emitter);
		root.detachChild(sound);
		root.removeControl(terminationControl);
	}

	UpdateControl terminationControl = new UpdateControl() {

		@Override
		public void update(final float tpf) {
			if (emitter.getNumVisibleParticles() == 0) {
				terminate();
			}
		}
	};

	private ParticleEmitter createEmitter() {
		final Texture texture = assets.loadTexture("Effects/Explosions/ashishlko11/Explosion.png");
		final Material material = new Material(assets, "Common/MatDefs/Misc/Particle.j3md");
		material.setTexture("Texture", texture);

		final ParticleEmitter emitter = new ParticleEmitter("ExplosionBlast", ParticleMesh.Type.Triangle, 5);
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

	private AudioNode createSound() {
		final AudioNode sound = new AudioNode(assets, "Sound/Effects/rock_breaking.ogg", DataType.Buffer);
		sound.setPositional(true);
		sound.setLooping(false);
		return sound;
	}


}