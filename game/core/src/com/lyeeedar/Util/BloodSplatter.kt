package com.lyeeedar.Util

import com.badlogic.gdx.math.Vector2
import com.lyeeedar.Components.*
import com.lyeeedar.Renderables.Animation.LeapAnimation
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.Systems.World
import ktx.math.times

class BloodSplatter
{
	companion object
	{
		val bloodArchetype = EntityArchetypeBuilder()
			.add(ComponentType.Renderable)
			.add(ComponentType.Position)

		val splatters = AssetManager.loadSprite("Oryx/Custom/terrain/bloodsplatter")

		fun splatter(source: Point, target: Point, emitDist: Float, world: World)
		{
			var vector = target.toVec()
			vector.sub(source.x.toFloat(), source.y.toFloat())
			vector.nor()

			val angle = 45f
			vector = vector.rotate(Random.random(Random.sharedRandom, -angle, angle))

			val dist = Random.randomWeighted(Random.sharedRandom) * emitDist
			vector = vector.scl(dist)

			val chosen = splatters.textures.random()

			val sprite = Sprite(chosen)
			sprite.baseScale[0] = 0.3f + Random.random(Random.sharedRandom, 0.3f)
			sprite.baseScale[1] = sprite.baseScale[0]
			sprite.rotation = Random.random(Random.sharedRandom, 180f)
			sprite.colour = Colour(1f, 0.6f + Random.random(Random.sharedRandom, 0.2f), 0.6f + Random.random(Random.sharedRandom, 0.2f), 0.4f + Random.random(Random.sharedRandom, 0.2f))

			val entity = bloodArchetype.build()

			val renderable = entity.renderable()!!
			renderable.data.renderable = sprite

			val pos = entity.pos()!!
			pos.position = target.copy()
			pos.offset.set(vector)

			val animDur = 0.1f + 0.15f * dist
			sprite.animation = LeapAnimation.obtain().set(animDur, arrayOf(vector * -1f, Vector2()), 0.1f + 0.1f * dist)
			sprite.animation!!.isBlocking = false

			world.addEntity(entity)
		}
	}
}