package com.lyeeedar.AI.Tasks

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Components.*
import com.lyeeedar.Systems.World
import com.lyeeedar.Util.AssetManager
import com.lyeeedar.Util.Colour
import com.lyeeedar.Util.Localisation
import squidpony.squidmath.LightRNG

class TaskInterrupt : AbstractTask()
{
	override fun execute(e: Entity, world: World<*>, rng: LightRNG)
	{
		var interrupted = false
		val activeAbility = e.actionSequence()
		if (activeAbility != null && activeAbility.actionSequence.cancellable)
		{
			activeAbility.actionSequence.cancel(activeAbility.actionSequenceState)
			e.removeComponent(ComponentType.ActionSequence)
			e.removeComponent(ComponentType.ActiveAbility)

			interrupted = true
		}

		val stunParticle = AssetManager.loadParticleEffect("StatusAndEffects/Stunned").getParticleEffect()
		stunParticle.addToWorld(world, e.position()!!.position, Vector2(0f, 0.8f))

		val message = if (interrupted) Localisation.getText("interrupted", "UI") else Localisation.getText("stunned", "UI")

		e.statistics()?.addMessage(message, Colour.YELLOW, 0.4f)
	}

	var obtained: Boolean = false
	companion object
	{
		private val pool: Pool<TaskInterrupt> = object : Pool<TaskInterrupt>() {
			override fun newObject(): TaskInterrupt
			{
				return TaskInterrupt()
			}
		}

		@JvmStatic fun obtain(): TaskInterrupt
		{
			val anim = pool.obtain()

			if (anim.obtained) throw RuntimeException()

			anim.obtained = true
			return anim
		}
	}
	override fun free() { if (obtained) { pool.free(this); obtained = false } }
}