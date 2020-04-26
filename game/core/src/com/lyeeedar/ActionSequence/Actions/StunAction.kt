package com.lyeeedar.ActionSequence.Actions

import com.lyeeedar.AI.Tasks.TaskInterrupt
import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.task
import com.lyeeedar.SpaceSlot
import com.lyeeedar.Util.XmlData

class StunAction : AbstractOneShotActionSequenceAction()
{
	var count: Int = 1

	override fun enter(state: ActionSequenceState): ActionState
	{
		for (target in state.targets)
		{
			val tile = state.world.grid.tryGet(target, null) ?: continue

			for (slot in SpaceSlot.EntityValues)
			{
				val entity = tile.contents[slot] ?: continue
				val task = entity.task() ?: continue

				for (i in 0 until count)
				{
					task.tasks.add(TaskInterrupt.obtain())
				}
			}
		}

		return ActionState.Completed
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		count = xmlData.getInt("Count", 1)
	}
	override val classID: String = "Stun"
	//endregion
}