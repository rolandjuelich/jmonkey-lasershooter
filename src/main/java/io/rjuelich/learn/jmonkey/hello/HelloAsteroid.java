package io.rjuelich.learn.jmonkey.hello;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class HelloAsteroid extends SimpleApplication {

	private static final String ACTION_FIRE = "fire";
	private Spatial asteroid;

	public static void main(final String[] args) {
		new HelloAsteroid().start();
	}

	private boolean alreadyPressed = false;
	private Beams beams = new Beams();
	private Target target;

	@Override
	public void simpleInitApp() {
		final Texture stars = getAssetManager().loadTexture("Scenes/starfield.png");
		final Texture planet = getAssetManager().loadTexture("Scenes/starfield-red-planet.png");
		final Spatial skybox = SkyFactory.createSky(getAssetManager(), stars, stars, stars, planet, stars, stars);
		getRootNode().attachChild(skybox);

		getRootNode().addLight(new DirectionalLight(new Vector3f(-1, -1, -.5f)));

		asteroid = getAssetManager().loadModel("Models/asteroid.blend");
		target = new Target(getAssetManager(), getRootNode(), asteroid);

		getInputManager().addMapping(ACTION_FIRE, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		getInputManager().addListener(registerPressedButton, ACTION_FIRE);
		getInputManager().addListener(fireLaser, ACTION_FIRE);

		final FilterPostProcessor fpp = new FilterPostProcessor(getAssetManager());
		final BloomFilter filter = new BloomFilter(BloomFilter.GlowMode.Objects);
		fpp.addFilter(filter);
		getViewPort().addProcessor(fpp);

	}

	@Override
	public void simpleUpdate(final float tpf) {
		beams.update();
		
		asteroid.rotate(0, .001f, 0);
	}

	private final ActionListener fireLaser = new ActionListener() {

		@Override
		public void onAction(final String name, final boolean isPressed, final float tpf) {
			if (isPressed && !alreadyPressed) {
				beams.add(new Beam(getAssetManager(), getRootNode(), getCamera(), target));
			}

		}
	};

	private final ActionListener registerPressedButton = new ActionListener() {

		@Override
		public void onAction(final String name, final boolean isPressed, final float tpf) {
			alreadyPressed = isPressed;
		}
	};
}
