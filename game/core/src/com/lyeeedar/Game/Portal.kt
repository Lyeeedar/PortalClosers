package com.lyeeedar.Game

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import com.lyeeedar.Screens.PortalScreen
import com.lyeeedar.Screens.ScreenEnum
import com.lyeeedar.Screens.WorldScreen
import com.lyeeedar.Util.Statics
import kotlin.random.Random

class Portal
{
	val encounters = Array<Array<Encounter>>()
	lateinit var current: Encounter

	private val connectionMap = mapOf(
			"1to2" to arrayOf(intArrayOf(0), intArrayOf(0,1)),
			"2to3" to arrayOf(intArrayOf(0,1), intArrayOf(1,2)),
			"3to4" to arrayOf(intArrayOf(0,1), intArrayOf(1,2), intArrayOf(2,3)),
			"4to3" to arrayOf(intArrayOf(0), intArrayOf(0,1), intArrayOf(1,2), intArrayOf(2)),
			"3to2" to arrayOf(intArrayOf(0), intArrayOf(0,1), intArrayOf(1)),
			"2to1" to arrayOf(intArrayOf(0), intArrayOf(0)))

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
		current.state = Encounter.EncounterState.CURRENT
		for (encounter in encounters[1])
		{
			encounter.state = Encounter.EncounterState.NEXT
		}

		for (i in 0 until encounters.size-1)
		{
			val row = encounters[i]
			val nextRow = encounters[i+1]

			val map = connectionMap["${row.size}to${nextRow.size}"]!!

			for (j in 0 until row.size)
			{
				val encounter = row[j]
				for (connection in map[j])
				{
					encounter.next.add(nextRow[connection])
				}
			}
		}
	}

	private fun generateEncounters(count: Int): Array<Encounter>
	{
		val output = Array<Encounter>()
		for (i in 0 until count)
		{
			output.add(Encounter())
		}
		for (encounter in output)
		{
			encounter.siblings.addAll(output)
			encounter.siblings.removeValue(encounter, true)
		}

		return output
	}

	fun completeEncounter(encounter: Encounter)
	{
		current.state = Encounter.EncounterState.COMPLETED
		current = encounter
		encounter.state = Encounter.EncounterState.CURRENT
		for (sibling in encounter.siblings)
		{
			sibling.state = Encounter.EncounterState.SKIPPED
		}
		if (encounter.next.size > 0)
		{
			for (row in encounter.next[0].siblings)
			{
				row.state = Encounter.EncounterState.SKIPPED
			}
			for (next in encounter.next)
			{
				next.state = Encounter.EncounterState.NEXT
			}
		}
	}
}

class Encounter
{
	enum class EncounterType
	{
		BATTLE,
		ELITE_BATTLE,
		BOSS_BATTLE,

		SIGIL_RECHARGE,
		STAT_REBALANCE,
		EVENT
	}

	enum class EncounterState
	{
		COMPLETED,
		SKIPPED,
		NEXT,
		FUTURE,
		CURRENT
	}
	var state = EncounterState.FUTURE
	var type = if (Random.nextFloat() < 0.1f) EncounterType.SIGIL_RECHARGE else EncounterType.BATTLE

	val siblings = Array<Encounter>()
	val next = Array<Encounter>()

	var animatedDrop = false

	val title: String
		get()
		{
			return when(type)
			{
				EncounterType.SIGIL_RECHARGE -> "Sigil Shrine"
				else -> "Battle"
			}
		}

	val description: String
		get()
		{
			return when (type)
			{
				EncounterType.SIGIL_RECHARGE -> "Your sigil resonates with the energy radiating from this shrine. Absorb it to recharge your sigil usages, or take its power to enhance your body."
				else -> "A pack of enemies fills the area, you are going to need to slay them to progress."
			}
		}

	fun createPreviewTable(): Table
	{
		return Table()
	}

	fun actions(screen: PortalScreen): Sequence<Pair<String, ()->Unit>>
	{
		return sequence {
			if (type == EncounterType.SIGIL_RECHARGE)
			{
				yield(Pair("Recharge Sigil") {
					screen.portal.completeEncounter(this@Encounter)
					screen.update()
				})
				yield(Pair("Absorb Energy") {
					screen.portal.completeEncounter(this@Encounter)
					screen.update()
				})
			}
			else
			{
				yield(Pair("Fight") {
//					val world = Statics.game.getTypedScreen<WorldScreen>()!!
//					world.baseCreate()
//					world.create()
//					world.completionCallback = {
//						Statics.game.switchScreen(ScreenEnum.PORTAL)
//						screen.portal.completeEncounter(this@Encounter)
//						screen.update()
//					}
//					Statics.game.switchScreen(ScreenEnum.WORLD)
					screen.portal.completeEncounter(this@Encounter)
					screen.update()
				})
			}
		}
	}
}