package com.lyeeedar.ActionSequence.Actions

import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.XmlData
import ktx.collections.set

@DataClass(category = "Meta", colour = "199,18,117", name = "MarkAndWait")
class MarkAndWaitForPlayerAction : AbstractOneShotActionSequenceAction()
{
	val key = "waitForPlayer"
	var turns: Int = 1

	override fun onTurn(state: ActionSequenceState): ActionState
	{
		val counter = state.data[key] as Int
		return if (counter <= 0) ActionState.Completed else ActionState.Blocked
	}

	override fun enter(state: ActionSequenceState)
	{
		state.data[key] = turns

		// also mark the tiles
	}

	override fun exit(state: ActionSequenceState): ActionState
	{
		val counter = state.data[key] as Int

		if (counter <= 0)
		{
			// remove tile mark

			state.data.remove(key)
			return ActionState.Completed
		}
		else
		{
			return ActionState.Blocked
		}
	}

	override fun preTurn(state: ActionSequenceState)
	{
		var counter = state.data[key] as Int
		counter--
		state.data[key] = counter
	}

	//region generated
	override fun load(xmlData: XmlData)
	{
		super.load(xmlData)
		turns = xmlData.getInt("Turns", 1)
	}
	override val classID: String = "MarkAndWaitForPlayer"
	//endregion
}