package com.lyeeedar.Screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.spine.*


class AnimationTestScreen : AbstractScreen()
{
	lateinit var batch: PolygonSpriteBatch
	lateinit var renderer: SkeletonRenderer
	lateinit var debugRenderer: SkeletonRendererDebug

	lateinit var atlas: TextureAtlas
	lateinit var skeleton: Skeleton
	lateinit var state: AnimationState

	override fun create()
	{
		batch = PolygonSpriteBatch()
		renderer = SkeletonRenderer()
		//renderer.setPremultipliedAlpha(true) // PMA results in correct blending without outlines.

		debugRenderer = SkeletonRendererDebug()
		debugRenderer.setBoundingBoxes(false)
		debugRenderer.setRegionAttachments(false)

		atlas = TextureAtlas(Gdx.files.internal("NewProject_1.atlas"))
		val json = SkeletonJson(atlas) // This loads skeleton JSON data, which is stateless.

		json.scale = 0.6f // Load the skeleton at 60% the size it was in Spine.

		val skeletonData: SkeletonData = json.readSkeletonData(Gdx.files.internal("NewProject_1.json"))

		skeleton = Skeleton(skeletonData) // Skeleton holds skeleton state (bone positions, slot attachments, etc).

		skeleton.setPosition(200f, 300f)

		val stateData = AnimationStateData(skeletonData) // Defines mixing (crossfading) between animations.

		stateData.setMix("idle", "walk", 0.2f)
		stateData.setMix("walk", "idle", 0.2f)

		state = AnimationState(stateData) // Holds the animation state for a skeleton (current animation, time, etc).

		state.setTimeScale(0.5f) // Slow all animations down to 50% speed.


		// Queue animations on track 0.

		// Queue animations on track 0.
		state.setAnimation(0, "idle", true)
		state.addAnimation(0, "walk", false, 2f) // Jump after 2 seconds.

		state.addAnimation(0, "idle", true, 0f) // Run after the jump.

		state.addAnimation(0, "walk", true, 6f) // Jump after 2 seconds.

	}

	override fun doRender(delta: Float)
	{
		state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.

		state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
		skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

		// Configure the camera, SpriteBatch, and SkeletonRendererDebug.
		batch.getProjectionMatrix().set(stage.camera.combined);
		debugRenderer.getShapeRenderer().setProjectionMatrix(stage.camera.combined);

		batch.begin();
		renderer.draw(batch, skeleton); // Draw the skeleton images.
		batch.end();

		debugRenderer.draw(skeleton); // Draw debug lines.
	}
}