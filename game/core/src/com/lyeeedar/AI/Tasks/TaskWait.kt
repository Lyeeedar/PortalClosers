package com.lyeeedar.AI.Tasks

import com.badlogic.gdx.utils.Pool
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.directionalSprite
import com.lyeeedar.Systems.World

class TaskWait(): AbstractTask()
{
	override fun execute(e: Entity, world: World)
	{
		e.directionalSprite()?.currentAnim = "wait"
	}

	var obtained: Boolean = false
	companion object
	{
		private val pool: Pool<TaskWait> = object : Pool<TaskWait>() {
			override fun newObject(): TaskWait
			{
				return TaskWait()
			}
		}

		@JvmStatic fun obtain(): TaskWait
		{
			val anim = pool.obtain()

			if (anim.obtained) throw RuntimeException()

			anim.obtained = true
			return anim
		}
	}
	override fun free() { if (obtained) { pool.free(this); obtained = false } }
}