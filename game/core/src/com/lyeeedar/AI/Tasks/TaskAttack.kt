package com.lyeeedar.AI.Tasks

import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Components.Entity
import com.lyeeedar.Systems.World
import squidpony.squidmath.LightRNG

class TaskAttack : AbstractTask()
{
	override fun execute(e: Entity, world: World, rng: LightRNG)
	{

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