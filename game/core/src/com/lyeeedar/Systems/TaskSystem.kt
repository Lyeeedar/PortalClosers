package com.lyeeedar.Systems

import com.lyeeedar.AI.Tasks.AbstractTask
import com.lyeeedar.AI.Tasks.TaskChannelAbility
import com.lyeeedar.AI.Tasks.TaskMove
import com.lyeeedar.Components.*
import com.lyeeedar.Game.Statistic

class TaskSystem(world: World<*>) : AbstractTaskSystem(world)
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
		}

		return 1f / if (speed < 0.2f) 0.2f else speed
	}

	override fun canStartTurn(): Boolean
	{
		for (x in 0 until world.grid.width)
		{
			for (y in 0 until world.grid.height)
			{
				val tile = world.grid[x, y]
				if (tile.queuedActions.size > 0)
				{
					return false
				}
			}
		}

		return true
	}

	override fun canEntityExecuteTask(entity: Entity, task: AbstractTask): Boolean
	{
		if (task is TaskMove)
		{
			val tile = world.grid.tryGet(entity.position()!!.position, null) ?: return true
			val next = world.grid.tryGet(tile, task.direction, null) ?: return true

			if (tile.tileContainsDelayedAction() || next.tileContainsDelayedAction()) return false
		}

		return true
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
		if (entity.activeAbility() != null)
		{
			entity.task()!!.tasks.add(TaskChannelAbility.obtain())
		}
		else
		{
			val ai = entity.ai()!!

			if (ai.isActive)
			{
				ai.ai.evaluate(ai.state)
			}
		}
	}
}