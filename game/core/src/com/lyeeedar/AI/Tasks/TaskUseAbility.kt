package com.lyeeedar.AI.Tasks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.ActionSequence.ActionSequence
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.Ability.Ability
import com.lyeeedar.Game.Ability.AbilityData
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Systems.*
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Localisation
import com.lyeeedar.Util.Random
import squidpony.squidmath.LightRNG

class TaskUseAbility : AbstractTask()
{
	lateinit var tile: AbstractTile
	lateinit var ability: Ability

	fun set(tile: AbstractTile, ability: Ability): TaskUseAbility
	{
		this.tile = tile
		this.ability = ability

		return this
	}

	override fun execute(e: Entity, world: World<*>, rng: LightRNG)
	{
		val pos = e.position() ?: return
		if (pos.moveLocked) return

		val stats = e.statistics()

		if (stats != null)
		{
			val distract = stats.getStat(Statistic.DISTRACTION)
			if (distract > 0f && Random.random(rng) < distract)
			{
				val stunParticle = AssetManager.loadParticleEffect("StatusAndEffects/Stunned").getParticleEffect()
				stunParticle.addToWorld(world, e.position()!!.position, Vector2(0f, 0.8f), isBlocking = false)

				ability.cooldown = ability.data.cooldown / 2

				stats.addMessage(Localisation.getText("distracted", "UI"), Colour.YELLOW, 0.4f)

				return
			}
		}

		if (EventSystem.isEventRegistered(EventType.USE_ABILITY, e))
		{
			world.eventSystem()?.addEvent(EventType.USE_ABILITY, e.getRef(), e.getRef())
		}

		pos.facing = Direction.getCardinalDirection(tile, pos.position)

		ability.cooldown = ability.data.cooldown
		ability.justUsed = true

		if (ability.remainingUsages > 0)
		{
			ability.remainingUsages--
		}

		val sequenceHolder = e.addOrGet(ComponentType.ActionSequence) as ActionSequenceComponent
		sequenceHolder.actionSequence = ability.data.actionSequence
		sequenceHolder.actionSequenceState.set(e.getRef(), world, rng.nextLong())
		sequenceHolder.actionSequenceState.targets.clear()
		sequenceHolder.actionSequenceState.targets.add(tile)
		sequenceHolder.actionSequenceState.facing = pos.facing

		sequenceHolder.actionSequence.update(0f, sequenceHolder.actionSequenceState)

		val abilityHolder = e.addOrGet(ComponentType.ActiveAbility) as ActiveAbilityComponent
		abilityHolder.ability = ability
	}

	var obtained: Boolean = false
	companion object
	{
		private val pool: Pool<TaskUseAbility> = object : Pool<TaskUseAbility>() {
			override fun newObject(): TaskUseAbility
			{
				return TaskUseAbility()
			}
		}

		@JvmStatic fun obtain(): TaskUseAbility
		{
			val anim = pool.obtain()

			if (anim.obtained) throw RuntimeException()

			anim.obtained = true
			return anim
		}
	}
	override fun free() { if (obtained) { pool.free(this); obtained = false } }
}