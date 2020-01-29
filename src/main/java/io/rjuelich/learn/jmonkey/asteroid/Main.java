package io.rjuelich.learn.jmonkey.asteroid;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

import io.rjuelich.learn.jmonkey.vessel.Interceptor;

public class Main extends SimpleApplication {

	private static final String YAW_LEFT = "yawLeft";
	private static final String YAW_RIGHT = "yawRight";
	private static final String MOVE_FORWARD = "moveForward";

	private static final String ACTION_FIRE = "fire";

	public static void main(final String[] args) {
		new Main().start();
	}

	private boolean alreadyPressed = false;
	private BulletAppState bulletAppState;

	private Interceptor vessel;

	@Override
	public void simpleInitApp() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

		final Texture stars = getAssetManager().loadTexture("Scenes/starfield.png");
		final Texture planet = getAssetManager().loadTexture("Scenes/starfield-red-planet.png");
		final Spatial skybox = SkyFactory.createSky(getAssetManager(), stars, stars, stars, planet, stars, stars);
		getRootNode().attachChild(skybox);

		getRootNode().addLight(new DirectionalLight(new Vector3f(-1, -1, -.5f)));

		vessel = new Interceptor(getAssetManager(), getRootNode());

		getInputManager().addMapping(ACTION_FIRE, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		getInputManager().addListener(registerPressedButton, ACTION_FIRE);
		getInputManager().addListener(fireLaser, ACTION_FIRE);

		getInputManager().addMapping(YAW_LEFT, new KeyTrigger(KeyInput.KEY_1));
		getInputManager().addListener(yawLeft, YAW_LEFT);

		getInputManager().addMapping(YAW_RIGHT, new KeyTrigger(KeyInput.KEY_3));
		getInputManager().addListener(yawRight, YAW_RIGHT);

		getInputManager().addMapping(MOVE_FORWARD, new KeyTrigger(KeyInput.KEY_2));
		getInputManager().addListener(moveForward, MOVE_FORWARD);

		final FilterPostProcessor fpp = new FilterPostProcessor(getAssetManager());
		final BloomFilter filter = new BloomFilter(BloomFilter.GlowMode.Objects);
		fpp.addFilter(filter);
		getViewPort().addProcessor(fpp);

	}

	private final AnalogListener yawRight = new AnalogListener() {
		@Override
		public void onAnalog(String name, float value, float tpf) {
			vessel.yawRight();
		}
	};

	private final AnalogListener yawLeft = new AnalogListener() {

		@Override
		public void onAnalog(String name, float value, float tpf) {
			vessel.yawLeft();
		}
	};

	private final AnalogListener moveForward = new AnalogListener() {
		
		@Override
		public void onAnalog(String name, float value, float tpf) {
			vessel.moveForward();
		}
	};

	private final ActionListener fireLaser = new ActionListener() {

		@Override
		public void onAction(final String name, final boolean isPressed, final float tpf) {
			if (isPressed && !alreadyPressed) {
				final Vector3f location = getCamera().getLocation().add(0, 1, 0);
				final Quaternion rotation = getCamera().getRotation();
				final Vector3f direction = getCamera().getDirection();
				new Beam(getAssetManager(), getRootNode(), location, rotation, direction,
						bulletAppState.getPhysicsSpace());
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
