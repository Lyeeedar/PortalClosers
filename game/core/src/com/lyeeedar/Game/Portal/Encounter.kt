package com.lyeeedar.Game.Portal

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.Array
import com.lyeeedar.Screens.PortalScreen
import kotlin.random.Random

enum class EncounterState
{
	COMPLETED,
	SKIPPED,
	NEXT,
	FUTURE,
	CURRENT
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