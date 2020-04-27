package com.lyeeedar.Systems

import com.lyeeedar.Components.*
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.BloodSplatter
import com.lyeeedar.Util.Random

class DeletionSystem(world: World<*>) : AbstractEntitySystem(world, world.getEntitiesFor().all(ComponentType.MarkedForDeletion).get())
{
	val eventListeners = world.getEntitiesFor().all(ComponentType.EventHandler).get()

	override fun updateEntity(entity: Entity, deltaTime: Float)
	{
		val pos = entity.position()
		pos?.removeFromTile(entity)

		val entityStats = entity.statistics()
		if (entityStats != null && entityStats.hp <= 0f)
		{
			if (pos != null)
			{
				val effect = AssetManager.loadParticleEffect("Death").getParticleEffect()
				effect.size[0] = pos.size
				effect.size[1] = pos.size
				effect.addToWorld(world, pos.position)

				// all the blood
				val numBlood = Random.random(Random.sharedRandom, 10, 20)
				for (i in 0 until numBlood)
				{
					BloodSplatter.splatter(entityStats.lastHitSource, pos.position, 3f, world)
				}
			}

			val killerTile = world.grid.tryGet(entityStats.lastHitSource, null)
			if (killerTile != null)
			{
				for (slot in SpaceSlot.EntityValues)
				{
					val e = killerTile.contents[slot]?.get() ?: continue
					val stats = e.statistics() ?: continue

					if (e.isEnemies(entity) && EventSystem.isEventRegistered(EventType.KILL, e))
					{
						// we have our killer!
						world.eventSystem()?.addEvent(EventType.KILL, EntityReference(e), EntityReference(entity))
					}
				}
			}

			// send death events
			for (listener in eventListeners.entities)
			{
				if (EventSystem.isEventRegistered(EventType.DEATH, listener))
				{
					world.eventSystem()?.addEvent(EventType.DEATH, EntityReference(listener), EntityReference(entity))
				}
			}
		}

		val activeAbility = entity.actionSequence()
		if (activeAbility != null)
		{
			activeAbility.actionSequence!!.cancel(activeAbility.actionSequenceState!!)
			activeAbility.actionSequenceState!!.free()
		}

		world.removeEntity(entity)
		entity.free()
	}

	override fun onTurnEntity(entity: Entity)
	{

	}
}