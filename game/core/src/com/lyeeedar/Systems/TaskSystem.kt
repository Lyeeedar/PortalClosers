package com.lyeeedar.Systems

import com.lyeeedar.AI.Tasks.AbstractTask
import com.lyeeedar.AI.Tasks.TaskAttack
import com.lyeeedar.AI.Tasks.TaskMove
import com.lyeeedar.Components.Entity
import com.lyeeedar.Components.statistics
import com.lyeeedar.Components.task
import com.lyeeedar.Game.Statistic

class TaskSystem(world: World) : AbstractTaskSystem(world)
{
	override fun getTaskCost(entity: Entity, task: AbstractTask): Float
	{
		var speed = 0f

		val stats = entity.statistics()
		if (stats != null)
		{
			speed += stats.getStat(Statistic.HASTE)

			if (task is TaskMove)
			{
				val fleet = stats.getStat(Statistic.FLEET_FOOT)
				speed += fleet
			}
			else if (task is TaskAttack)
			{
				val dervish = stats.getStat(Statistic.DERVISH)
				speed += dervish
			}
		}

		return 1f / if (speed < 0.2f) 0.2f else speed
	}

	override fun getPlayerActionAmount(): Float
	{
		val player = world.player!!
		val processState = processEntity(player)

		if (processState == ProcessEntityState.SUCCESS)
		{
			val task = player.task()!!
			val actionAmount = task.actionAccumulator * -1f
			task.actionAccumulator = 0f

			return actionAmount
		}

		return -1f
	}

	override fun doEntityAI(entity: Entity)
	{

	}
}