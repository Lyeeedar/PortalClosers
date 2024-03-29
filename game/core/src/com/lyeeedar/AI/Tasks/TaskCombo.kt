package com.lyeeedar.AI.Tasks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Components.*
import com.lyeeedar.Direction
import com.lyeeedar.Game.Combo.ComboStep
import com.lyeeedar.Game.Statistic
import com.lyeeedar.Game.Tile
import com.lyeeedar.Systems.EventSystem
import com.lyeeedar.Systems.EventType
import com.lyeeedar.Systems.World
import com.lyeeedar.Systems.eventSystem
import com.lyeeedar.Util.*
import squidpony.squidmath.LightRNG

class TaskCombo(): AbstractTask()
{
	lateinit var comboStep: ComboStep
	lateinit var tile: Tile
	var storedTarget: Point? = null

	fun set(tile: Tile, comboStep: ComboStep, storedTarget: Point?): TaskCombo
	{
		this.tile = tile
		this.comboStep = comboStep
		this.storedTarget = storedTarget

		return this
	}

	override fun execute(e: Entity, world: World<*>, rng: LightRNG)
	{
		val comboHolder = e.combo()!!
		val pos = e.position()!!
		val stats = e.statistics()

		comboHolder.currentStep = comboStep
		comboHolder.lastTarget = tile

		var direction = Direction.getCardinalDirection(tile, pos.position)
		pos.facing = direction

		if (!comboStep.canTurn)
		{
			comboHolder.fixedDirection = direction
		}
		else
		{
			comboHolder.fixedDirection = null
		}

		var delay = 0f
		if (comboStep.stepForward && !pos.moveLocked)
		{
			var canMove = true
			if (stats != null)
			{
				val root = stats.getStat(Statistic.ROOT)
				if (root > 0f && Random.random(rng) < root)
				{
					val stunParticle = AssetManager.loadParticleEffect("StatusAndEffects/Stunned").getParticleEffect()
					stunParticle.addToWorld(world, e.position()!!.position, Vector2(0f, 0.8f), isBlocking = false)

					stats.addMessage(Localisation.getText("rooted", "UI"), Colour.YELLOW, 0.4f)

					canMove = false
				}
			}

			if (canMove)
			{
				if (EventSystem.isEventRegistered(EventType.MOVE, e))
				{
					val ref = e.getRef()
					world.eventSystem()?.addEvent(EventType.MOVE, ref, ref)
				}

				val didMove = pos.moveInDirection(direction, e, world)

				if (didMove)
				{
					tile = world.grid.getClamped(tile, direction) as Tile

					if (comboHolder.fixedDirection == null)
					{
						tile = comboStep.getAsAbility().getValidTile(e, world, world.rng, storedTarget, comboHolder.fixedDirection) ?: tile
						direction = Direction.getCardinalDirection(tile, pos.position)
						pos.facing = direction
					}

					delay = 0.4f
				}
			}
		}
		comboHolder.lastTarget = tile

		val ability = comboStep.getAsAbility()
		var canUseAbility = true
		if (stats != null)
		{
			val distract = stats.getStat(Statistic.DISTRACTION)
			if (distract > 0f && Random.random(rng) < distract)
			{
				val stunParticle = AssetManager.loadParticleEffect("StatusAndEffects/Stunned").getParticleEffect()
				stunParticle.addToWorld(world, e.position()!!.position, Vector2(0f, 0.8f), isBlocking = false)

				ability.cooldown = ability.data.cooldown / 2

				stats.addMessage(Localisation.getText("distracted", "UI"), Colour.YELLOW, 0.4f)

				canUseAbility = false
			}
		}

		if (canUseAbility)
		{
			if (EventSystem.isEventRegistered(EventType.ATTACK, e))
			{
				world.eventSystem()?.addEvent(EventType.ATTACK, e.getRef(), e.getRef())
			}

			ability.cooldown = ability.data.cooldown
			ability.justUsed = true

			if (ability.remainingUsages > 0)
			{
				ability.remainingUsages--
			}

			val sequenceHolder = e.addOrGet(ComponentType.ActionSequence) as ActionSequenceComponent
			sequenceHolder.actionSequence = ability.data.actionSequence
			sequenceHolder.actionSequenceState.set(e.getRef(), sequenceHolder.actionSequence, world, rng.nextLong())
			sequenceHolder.actionSequenceState.targets.clear()
			sequenceHolder.actionSequenceState.targets.add(tile)
			sequenceHolder.actionSequenceState.facing = pos.facing
			sequenceHolder.actionSequenceState.delay = delay

			sequenceHolder.actionSequence.update(0f, sequenceHolder.actionSequenceState)

			val abilityHolder = e.addOrGet(ComponentType.ActiveAbility) as ActiveAbilityComponent
			abilityHolder.ability = ability
		}
	}

	var obtained: Boolean = false
	companion object
	{
		private val pool: Pool<TaskCombo> = object : Pool<TaskCombo>() {
			override fun newObject(): TaskCombo
			{
				return TaskCombo()
			}
		}

		@JvmStatic fun obtain(): TaskCombo
		{
			val anim = pool.obtain()

			if (anim.obtained) throw RuntimeException()

			anim.obtained = true
			return anim
		}
	}
	override fun free() { if (obtained) { pool.free(this); obtained = false } }
}