package com.lyeeedar.AI.Tasks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectSet
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.*
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Renderables.Animation.BumpAnimation
import com.lyeeedar.Renderables.Animation.MoveAnimation
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.*
import squidpony.squidmath.LightRNG

class TaskAttack : AbstractTask()
{
	lateinit var tile: AbstractTile
	lateinit var attackDefinition: AttackDefinition

	fun set(tile: AbstractTile, attackDefinition: AttackDefinition): TaskAttack
	{
		this.tile = tile
		this.attackDefinition = attackDefinition

		return this
	}

	override fun execute(e: Entity, world: World<*>, rng: LightRNG)
	{
		val pos = e.position()!!
		val stats = e.statistics()!!

		val fumble = stats.getStat(Statistic.FUMBLE)
		if (fumble > 0f && Random.random(rng) < fumble)
		{
			val stunParticle = AssetManager.loadParticleEffect("StatusAndEffects/Stunned").getParticleEffect()
			stunParticle.addToWorld(world, pos.position, Vector2(0f, 0.8f), isBlocking = false)

			stats.addMessage(Localisation.getText("fumbled", "UI"), Colour.YELLOW, 0.4f)

			return
		}

		pos.facing = Direction.getCardinalDirection(tile, pos.position)

		e.renderable()!!.renderable.animation = BumpAnimation.obtain().set(0.2f, Direction.getDirection(pos.position, tile))

		val entitiesToHit = ObjectSet<EntityReference>()
		for (slot in SpaceSlot.EntityValues)
		{
			val entity = tile.contents[slot]?.get() ?: continue
			if (e.isEnemies(entity))
			{
				entitiesToHit.add(entity.getRef())
			}
		}

		val eventSystem = world.eventSystem()!!
		if (EventSystem.isEventRegistered(EventType.ATTACK, e))
		{
			for (entity in entitiesToHit)
			{
				eventSystem.addEvent(EventType.ATTACK, e.getRef(), entity)
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

			effect.addToWorld(world, tile, Vector2(0f, 0.5f))

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

		val attacker = e.getRef()
		tile.addDelayedAction(
			{
				val e = attacker.get()
				if (e != null)
				{
					for (entity in entitiesToHit)
					{
						if (!entity.isValid()) continue

						// TODO: Included weapon dam in this. Maybe 'BaseAttack = WeaponAttack+(StatAtk*LevelMult)'
						val baseAttack = e.statistics()!!.getStat(Statistic.ATK_POWER) * stats.attackDefinition.damage

						val dam = DamageEquations.getAttackDam(rng, baseAttack)
						DamageEquations.doAttack(rng, e, entity.entity, AttackDamage(dam, stats.attackDefinition.type), world)
					}
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