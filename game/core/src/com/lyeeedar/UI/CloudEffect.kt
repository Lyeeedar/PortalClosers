package com.lyeeedar.UI

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ObjectMap
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Random
import com.lyeeedar.Util.max
import ktx.collections.set

class CloudEffect(var noiseScale: Float = 1f, var scrollSpeed: Float = 5f) : Actor()
{
	val gradient = AssetManager.loadTextureRegion("darkest/cloud_gradient")
	val shader = AssetManager.loadShader("Sprites/Shaders/cloud_effect")

	val offset = Vector3()
	val scrollVector = Vector2().setToRandomDirection().scl(scrollSpeed)

	override fun act(delta: Float)
	{
		offset.x += scrollVector.x * delta
		offset.y += scrollVector.y * delta
		offset.z += delta * (scrollSpeed * 0.01f)
	}

	override fun draw(batch: Batch, parentAlpha: Float)
	{
		batch.shader = shader

		shader.setUniformf("u_scale_z_offset", noiseScale, offset.z, offset.x + x, offset.y + y)
		shader.setUniformf("u_gradientTexCoords", gradient.u, gradient.u2, gradient.v)

		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
		batch.draw(gradient, 0f, 0f, stage.width, stage.height)

		batch.shader = null
	}
}