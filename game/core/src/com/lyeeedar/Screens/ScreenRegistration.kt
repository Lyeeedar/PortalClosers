package com.lyeeedar

import com.lyeeedar.Screens.*
import com.lyeeedar.Util.Statics
import java.util.*

enum class ScreenEnum
{
	WORLD,
	PARTICLEEDITOR,
	MAPGENERATIONPREVIEW,
	INVALID
}

fun registerDebugScreens(): HashMap<ScreenEnum, AbstractScreen>
{
	val screens = HashMap<ScreenEnum, AbstractScreen>()

	if (!Statics.android)
	{
		screens[ScreenEnum.PARTICLEEDITOR] = ParticleEditorScreen()
		screens[ScreenEnum.MAPGENERATIONPREVIEW] = MapGenerationPreviewScreen()
	}

	return screens
}

fun registerGameScreens(): HashMap<ScreenEnum, AbstractScreen>
{
	val screens = HashMap<ScreenEnum, AbstractScreen>()

	screens[ScreenEnum.WORLD] = WorldScreen()

	return screens
}