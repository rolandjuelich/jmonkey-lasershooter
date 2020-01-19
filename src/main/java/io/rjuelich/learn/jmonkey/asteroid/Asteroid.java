package io.rjuelich.learn.jmonkey.asteroid;

import static com.jme3.bullet.util.CollisionShapeFactory.createDynamicMeshShape;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LodControl;
import com.jme3.scene.control.UpdateControl;

import jme3tools.optimize.LodGenerator;

public class Asteroid {

	public static final int MAX_HIT_COUNT = 30;

	private int hitCount = 0;

	private final AssetManager assets;
	private final Node root;

	private final Spatial model;
	private final PhysicsSpace physics;
	private final GhostControl physicsControl;

	public Asteroid(final AssetManager assetManager, final Node rootNode, final PhysicsSpace physicsSpace) {
		this.assets = assetManager;
		this.root = rootNode;
		this.physics = physicsSpace;

		this.model = assetManager.loadModel("Models/asteroid.blend");
		this.model.setName("asteroid");
		this.model.depthFirstTraversal(new SceneGraphVisitorAdapter() {
			@Override
			public void visit(final Geometry geom) {
				final LodGenerator lodGenerator = new LodGenerator(geom);
				lodGenerator.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, .25f, .5f, .75f);
				geom.addControl(new LodControl());
			}
		});

		this.physicsControl = new GhostControl(createDynamicMeshShape(model));

		this.model.addControl(rotationControl);
		this.model.addControl(physicsControl);

		this.physics.add(physicsControl);
		this.physics.addCollisionListener(collissionHandler);

		this.root.attachChild(model);

		// preload to improve performance => nothing will be attached to rootNode
		new Explosion(assetManager, rootNode);
		new ExplosionBlast(assetManager, rootNode);
	}

	UpdateControl rotationControl = new UpdateControl() {
		@Override
		public void update(final float tpf) {
			model.rotate(0, .001f, 0);
		}
	};

	PhysicsCollisionListener collissionHandler = new PhysicsCollisionListener() {

		@Override
		public void collision(final PhysicsCollisionEvent event) {
			if (hitCount < MAX_HIT_COUNT) {
				sufferDamage(event.getNodeB().getWorldTranslation());
			} else {
				explode(event.getNodeB().getWorldTranslation());
			}
		}
	};

	public void explode(final Vector3f location) {

		// physicsSpace.removeCollisionListener(collissionHandler);

		// TODO figure out why "java.lang.IndexOutOfBoundsException"
		// happens here when the listener gets removed

		physics.remove(physicsControl);

		root.detachChild(model);
		root.removeControl(physicsControl);

		new ExplosionBlast(assets, root).at(location);

	}

	public void sufferDamage(final Vector3f location) {
		hitCount++;
		new Explosion(assets, root).at(location);
	}

}
