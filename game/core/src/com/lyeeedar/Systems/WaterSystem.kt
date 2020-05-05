package com.lyeeedar.Systems

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectSet
import com.lyeeedar.AI.Tasks.TaskInterrupt
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Renderables.Animation.MoveAnimation
import com.lyeeedar.Renderables.Animation.SpinAnimation
import com.lyeeedar.Renderables.Particle.ParticleEffect
import com.lyeeedar.Renderables.Sprite.Sprite
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.removeRandom
import ktx.collections.set

class WaterSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().all(ComponentType.Water).get())
{
	val rippleKey = "WaterRipple"
	val rippleParticle = AssetManager.loadParticleEffect("World/WaterRipple").getParticleEffect()

	val lastInWaterEntities = ObjectSet<EntityReference>()
	val inWaterEntities = ObjectSet<EntityReference>()

	val movedByWater = ObjectSet<Entity>()

	val metaRegions = world.getEntitiesFor().all(ComponentType.MetaRegion, ComponentType.Position).get()

	// Update
	override fun beforeUpdate(deltaTime: Float)
	{
		inWaterEntities.clear()
	}

	override fun afterUpdate(deltaTime: Float)
	{
		if (inWaterEntities.size == 0 && lastInWaterEntities.size == 0) return

		// clear ripples from old entities
		for (entityRef in lastInWaterEntities)
		{
			val entity = entityRef.get()
			if (entity != null && !inWaterEntities.contains(entityRef))
			{
				removeRipples(entity)
			}
		}

		// update list
		lastInWaterEntities.clear()
		lastInWaterEntities.addAll(inWaterEntities)
	}

	fun removeRipples(entity: Entity)
	{
		val additional = entity.additionalRenderable()

		if (additional != null && additional.below.containsKey(rippleKey))
		{
			val ripple = additional.below[rippleKey] as ParticleEffect
			additional.below.remove(rippleKey)

			ripple.stop()

			ripple.addToWorld(world, entity.position()!!.position, slot = SpaceSlot.BELOWENTITY)
		}

		val renderable = entity.renderable() ?: return
		val sprite = renderable.renderable as? Sprite ?: return

		sprite.removeAmount = 0f
	}

	fun addRipples(entity: Entity, depth: Float)
	{
		if (depth >= 0.4f)
		{
			val additional = entity.addOrGet(ComponentType.AdditionalRenderable) as AdditionalRenderableComponent

			if (!additional.below.containsKey(rippleKey))
			{
				val ripple = rippleParticle.copy()
				ripple.size[0] = entity.position()!!.size
				ripple.size[1] = ripple.size[0]
				ripple.loop = true
				ripple.start()

				additional.below[rippleKey] = ripple
			}
		}

		val renderable = entity.renderable() ?: return
		val sprite = renderable.renderable as? Sprite ?: return

		sprite.removeAmount = depth / entity.position()!!.size
	}

	override fun updateEntity(entity: Entity, deltaTime: Float)
	{
		val water = entity.water()!!

		val tile = world.grid.tryGet(entity.position()!!.position, null) ?: return
		if (tile.dist(world.player!!.position()!!.position) > 40) return

		for (slot in SpaceSlot.EntityValues)
		{
			val otherRef = tile.contents[slot] ?: continue
			val other = otherRef.get() ?: continue

			if (other.renderable() != null)
			{
				inWaterEntities.add(otherRef)

				addRipples(other, water.depth)
			}
		}
	}

	// Turn
	override fun beforeOnTurn()
	{
		movedByWater.clear()
	}

	fun hasWater(tile: AbstractTile): Boolean
	{
		for (slot in SpaceSlot.Values)
		{
			val entity = tile.contents[slot]?.get() ?: continue
			if (entity.hasComponent(ComponentType.Water)) return true
		}

		return false
	}

	override fun onTurnEntity(entity: Entity)
	{
		val water = entity.water()!!
		val tile = world.grid.tryGet(entity.position()!!.position, null) ?: return

		if (water.flowDir == Direction.CENTER && water.flowTowards == null) return
		if (water.flowForce == 0 || water.flowChance == 0f) return

		var direction = water.flowDir
		if (water.flowTowards != null)
		{
			val closest = metaRegions.entities.filter { it.metaRegion()!!.keys.contains(water.flowTowards!!) }.minBy { it.position()!!.position.dist(tile) } ?: return
			direction = Direction.getDirection(tile, closest.position()!!.position)
		}

		val prev = tile
		val next = world.grid.tryGet(prev, direction, null) ?: return

		for (slot in SpaceSlot.EntityValues)
		{
			val otherRef = tile.contents[slot] ?: continue
			val other = otherRef.get() ?: continue
			val otherPos = other.position() ?: continue

			if (movedByWater.contains(other)) continue

			val pos = other.position() ?: continue
			if (!pos.moveable || pos.moveLocked) continue

			val renderable = other.renderable() ?: continue

			movedByWater.add(other)

			val doFlow = world.rng.nextFloat()
			if (doFlow <= water.flowChance)
			{
				// if we have force, then fling the entity in this direction
				if (water.flowForce > 1)
				{
					var dest = prev
					for (i in 0 until water.flowForce)
					{
						val nextDest = world.grid.tryGet(dest, direction, null) ?: break
						if (!otherPos.isValidTile(nextDest, other)) break

						dest = nextDest
					}

					if (dest != prev)
					{
						otherPos.doMove(dest, other)

						renderable.renderable.animation = MoveAnimation.obtain().set(dest, prev, 0.15f)
						renderable.renderable.animation = SpinAnimation.obtain().set(0.15f, 180f)

						(renderable.renderable as? Sprite)?.removeAmount = 0f

						other.task()?.tasks?.add(TaskInterrupt.obtain())
					}
				}
				else
				{
					// otherwise try to push them to more water

					var dest = next
					if (!hasWater(dest))
					{
						// try clockwise / anti
						val valid = Array<AbstractTile>(2)
						val cw = world.grid.tryGet(prev, direction.cardinalClockwise, null)
						val ccw = world.grid.tryGet(prev, direction.cardinalAnticlockwise, null)
						if (cw != null && hasWater(cw)) valid.add(cw)
						if (ccw != null && hasWater(ccw)) valid.add(ccw)

						if (valid.size == 0) return

						dest = valid.removeRandom(world.rng)
					}

					if (otherPos.isValidTile(dest, other))
					{
						otherPos.doMove(dest, other)

						renderable.renderable.animation = MoveAnimation.obtain().set(dest, prev, 0.15f)
					}
				}
			}
		}
	}
}