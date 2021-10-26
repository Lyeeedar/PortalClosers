package com.lyeeedar.Game.Portal

import com.badlogic.gdx.utils.Array
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.statistics

class Portal
{
	val encounters = Array<Array<AbstractEncounter>>()
	lateinit var current: AbstractEncounter

	private val connectionMap = mapOf(
			"1to2" to arrayOf(intArrayOf(0), intArrayOf(0,1)),
			"2to3" to arrayOf(intArrayOf(0,1), intArrayOf(1,2)),
			"3to4" to arrayOf(intArrayOf(0,1), intArrayOf(1,2), intArrayOf(2,3)),
			"4to3" to arrayOf(intArrayOf(0), intArrayOf(0,1), intArrayOf(1,2), intArrayOf(2)),
			"3to2" to arrayOf(intArrayOf(0), intArrayOf(0,1), intArrayOf(1)),
			"2to1" to arrayOf(intArrayOf(0), intArrayOf(0)))

	var player = EntityLoader.load("Entities/player")

	init
	{
		player.statistics()!!.calculateStatistics(1)
	}

	fun generate(length: Int)
	{
		encounters.add(generateEncounters(1))
		encounters.add(generateEncounters(2))

		var is3 = true
		for (i in 0 until length-4)
		{
			if (is3)
			{
				encounters.add(generateEncounters(3))
			}
			else
			{
				encounters.add(generateEncounters(4))
			}

			is3 = !is3
		}

		encounters.add(generateEncounters(3))
		encounters.add(generateEncounters(2))
		encounters.add(generateEncounters(1))

		current = encounters[0][0]
		current.state = EncounterState.CURRENT
		for (AbstractEncounter in encounters[1])
		{
			AbstractEncounter.state = EncounterState.NEXT
		}

		for (i in 0 until encounters.size-1)
		{
			val row = encounters[i]
			val nextRow = encounters[i+1]

			val map = connectionMap["${row.size}to${nextRow.size}"]!!

			for (j in 0 until row.size)
			{
				val AbstractEncounter = row[j]
				for (connection in map[j])
				{
					AbstractEncounter.next.add(nextRow[connection])
				}
			}
		}
	}

	private fun generateEncounters(count: Int): Array<AbstractEncounter>
	{
		val output = Array<AbstractEncounter>()
		for (i in 0 until count)
		{
			output.add(CombatEncounter())
		}
		for (AbstractEncounter in output)
		{
			AbstractEncounter.siblings.addAll(output)
			AbstractEncounter.siblings.removeValue(AbstractEncounter, true)
		}

		return output
	}

	fun completeEncounter(AbstractEncounter: AbstractEncounter)
	{
		current.state = EncounterState.COMPLETED
		current = AbstractEncounter
		AbstractEncounter.state = EncounterState.CURRENT
		for (sibling in AbstractEncounter.siblings)
		{
			sibling.state = EncounterState.SKIPPED
		}
		if (AbstractEncounter.next.size > 0)
		{
			for (row in AbstractEncounter.next[0].siblings)
			{
				row.state = EncounterState.SKIPPED
			}
			for (next in AbstractEncounter.next)
			{
				next.state = EncounterState.NEXT
			}
		}
	}
}

