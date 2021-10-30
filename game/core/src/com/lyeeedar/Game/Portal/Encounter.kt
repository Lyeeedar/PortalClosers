package com.lyeeedar.Game.Portal

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.Array
import com.lyeeedar.Screens.PortalScreen
import com.lyeeedar.Util.random
import ktx.collections.toGdxArray
import squidpony.squidmath.LightRNG
import kotlin.random.Random

enum class EncounterState
{
	COMPLETED,
	SKIPPED,
	NEXT,
	FUTURE,
	CURRENT
}

enum class EncounterType
{
	BOSS,
	ELITE,
	NORMAL,
	EVENT,
	SIGIL_SHRINE, // recharge
	SAFE_ZONE, // heal or rearrange stats
	UNKNOWN
}

abstract class AbstractEncounter
{
	var state = EncounterState.FUTURE

	val siblings = Array<AbstractEncounter>()
	val next = Array<AbstractEncounter>()

	var animatedDrop = false

	abstract val title: String
	abstract val description: String

	abstract fun createPreviewTable(): Table
	abstract fun actions(screen: PortalScreen): Sequence<Pair<String, ()->Unit>>
	abstract fun getMapWidget(): Widget
}

fun createEncounter(allowedTypes: Array<EncounterType>, biome: Biome, rng: LightRNG): AbstractEncounter
{
	val selectedType = allowedTypes.random(rng)

	return when (selectedType)
	{
		EncounterType.UNKNOWN -> {
			val actual = createEncounter(allowedTypes.filter { it != EncounterType.UNKNOWN }.toGdxArray(), biome, rng)
			UnknownEncounter(actual)
		}
		EncounterType.NORMAL -> {
			CombatEncounter(biome, biome.normalPacks.random(rng))
		}
		EncounterType.BOSS -> {
			BossEncounter()
		}
		EncounterType.ELITE -> {
			EliteEncounter()
		}
		EncounterType.SIGIL_SHRINE -> {
			SigilEncounter()
		}
		EncounterType.SAFE_ZONE -> {
			SafeZoneEncounter()
		}
		EncounterType.EVENT -> {
			EventEncounter()
		}
	}
}