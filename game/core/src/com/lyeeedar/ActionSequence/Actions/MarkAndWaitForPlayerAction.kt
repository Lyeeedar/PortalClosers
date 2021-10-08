package com.lyeeedar.ActionSequence.Actions

import com.lyeeedar.ActionSequence.ActionSequenceState
import com.lyeeedar.Components.position
import com.lyeeedar.Game.Tile
import com.lyeeedar.Util.DataClass
import com.lyeeedar.Util.XmlData
import ktx.collections.set

@DataClass(category = "Meta", colour = "199,18,117", name = "MarkAndWait")
class MarkAndWaitForPlayerAction : AbstractOneShotActionSequenceAction()
{
	val key = "waitForPlayer"
	var turns: Int = 1

	override fun isBlocked(state: ActionSequenceState): Boolean
	{
		if (!state.detached && state.source.isValid())
		{
			// check if the source entity is being acted on
			val pos = state.source.get()?.position()?.position
			if (pos != null)
			{
				val tile = state.world.grid.tryGet(pos, null)
				if (tile != null)
				{
					return tile.tileContainsDelayedAction()
				}
			}
		}

		val counter = state.data[key] as Int? ?: 0
		return counter > 0
	}

	override fun enter(state: ActionSequenceState)
	{
		state.data[key] = turns

		// also mark the tiles
		for (point in state.targets)
		{
			val tile = state.world.grid[point] as? Tile ?: continue
			tile.predictedAttacksFrom.add(state.getRef())
			tile.isTileDirty = true
		}
	}

	override fun exit(state: ActionSequenceState)
	{
		val counter = state.data[key] as Int

		if (counter <= 0)
		{
			removeTileMark(state)

			state.data.remove(key)
		}
	}

	override fun cancel(state: ActionSequenceState)
	{
		removeTileMark(state)
	}

	private fun removeTileMark(state: ActionSequenceState)
	{
		// remove tile mark
		for (point in state.targets)
		{
			val tile = state.world.grid[point] as? Tile ?: continue
			tile.predictedAttacksFrom.remove(state.getRef())
			tile.isTileDirty = true
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