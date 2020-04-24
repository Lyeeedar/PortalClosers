package com.lyeeedar.AI.Tasks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectSet
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.AttackDamage
import com.lyeeedar.Game.DamageEquations
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Game.Tile
import com.lyeeedar.Renderables.Animation.BumpAnimation
import com.lyeeedar.Renderables.Animation.MoveAnimation
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.*
import squidpony.squidmath.LightRNG

class TaskAttack : AbstractTask()
{
	lateinit var tile: Tile
	lateinit var attackDefinition: AttackDefinition

	fun set(tile: Tile, attackDefinition: AttackDefinition): TaskAttack
	{
		this.tile = tile
		this.attackDefinition = attackDefinition

		return this
	}

	override fun execute(e: Entity, world: World, rng: LightRNG)
	{
		val pos = e.pos()!!
		val stats = e.stats()!!

		val fumble = stats.getStat(Statistic.FUMBLE)
		if (fumble > 0f && Random.random(rng) < fumble)
		{
			val stunParticle = AssetManager.loadParticleEffect("Stunned").getParticleEffect()
			stunParticle.addToWorld(world, pos.position, Vector2(0f, 0.8f))

			stats.addMessage("Fumbled!", Colour.YELLOW, 0.4f)

			return
		}

		pos.facing = Direction.getCardinalDirection(tile, pos.position)

		e.renderable()!!.renderable.animation = BumpAnimation.obtain().set(0.2f, Direction.getDirection(pos.position, tile))

		val entitiesToHit = ObjectSet<Entity>()
		for (slot in SpaceSlot.EntityValues)
		{
			val entity = tile.contents[slot] ?: continue
			if (e.isAllies(entity)) continue

			entitiesToHit.add(entity)
		}

		val eventSystem = world.eventSystem()!!
		if (EventSystem.isEventRegistered(EventType.ATTACK, e))
		{
			for (entity in entitiesToHit)
			{
				val eventData = EventData.obtain().set(EventType.ATTACK, e, entity)
				eventSystem.addEvent(eventData)
			}
		}

		val diff = tile.getPosDiff(pos.position)

		var delay = 0f

		if (attackDefinition.range > 1)
		{
			val animDuration = 0.15f + tile.euclideanDist(pos.position) * 0.015f

			val effect = attackDefinition.flightEffect!!.getParticleEffect()
			effect.rotation = getRotation(pos.position, tile)
			effect.killOnAnimComplete = true
			effect.animation = MoveAnimation.obtain().set(animDuration, diff)

			effect.addToWorld(world, tile)

			delay += animDuration
		}

		if (attackDefinition.hitEffect != null)
		{
			val effect = attackDefinition.hitEffect!!.getParticleEffect()
			effect.renderDelay = delay
			effect.rotation = getRotation(pos.position, tile)
			effect.addToWorld(world, tile)

			delay += effect.blockinglifetime * 0.7f
		}

		tile.addDelayedAction(
			{
				for (entity in entitiesToHit)
				{
					// TODO: Included weapon dam in this. Maybe 'BaseAttack = WeaponAttack+(StatAtk*LevelMult)'
					val baseAttack = e.stats()!!.getStat(Statistic.ATK_POWER) * stats.data.attackDefinition.damage

					val dam = DamageEquations.getAttackDam(rng, e.stats()!!, AttackDamage(baseAttack, stats.data.attackDefinition.type))
					DamageEquations.doAttack(rng, e, entity, dam, world)
				}
			}, delay)
	}

	var obtained: Boolean = false
	companion object
	{
		private val pool: Pool<TaskAttack> = object : Pool<TaskAttack>() {
			override fun newObject(): TaskAttack
			{
				return TaskAttack()
			}
		}

		@JvmStatic fun obtain(): TaskAttack
		{
			val anim = pool.obtain()

			if (anim.obtained) throw RuntimeException()

			anim.obtained = true
			return anim
		}
	}
	override fun free() { if (obtained) { pool.free(this); obtained = false } }
}