package io.rjuelich.learn.jmonkey.vessel;

import static com.jme3.bullet.util.CollisionShapeFactory.createDynamicMeshShape;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.control.UpdateControl;

public class Shield {

	private static final int MAX_SHIELD_TTL = 10;

	private final Node node;
	private final GhostControl collisionControl;
	private int shieldTtl = 0;

	private Geometry model;

	public Shield(final Node node, final PhysicsSpace physics) {
		this.node = node;

		node.depthFirstTraversal(new SceneGraphVisitorAdapter() {
			@Override
			public void visit(final Geometry geom) {
				if (geom != null && geom.getName() != null && geom.getName().toLowerCase().contains("shieldgeom")) {
					geom.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Additive);
					geom.getMaterial().getAdditionalRenderState().setDepthWrite(false);
					geom.getMaterial().getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
					geom.setQueueBucket(Bucket.Transparent);
					model = geom;
				}
			}

		});

		this.collisionControl = new GhostControl(createDynamicMeshShape(node));
		node.addControl(collisionControl);

		physics.add(collisionControl);
		physics.addCollisionListener(collissionHandler);

		node.addControl(shieldControl);

	}

	PhysicsCollisionListener collissionHandler = new PhysicsCollisionListener() {

		@Override
		public void collision(final PhysicsCollisionEvent event) {
			System.out.println("shield hit! " + event.getLocalPointA());
			shieldTtl = MAX_SHIELD_TTL;
		}
	};

	UpdateControl shieldControl = new UpdateControl() {
		public void update(final float tpf) {

			if (shieldTtl == MAX_SHIELD_TTL) {
				node.attachChild(model);
			}

			if (shieldTtl == 0) {
				node.detachChild(model);
			}

			if (shieldTtl > 0) {
				shieldTtl--;
			}

		};
	};
}