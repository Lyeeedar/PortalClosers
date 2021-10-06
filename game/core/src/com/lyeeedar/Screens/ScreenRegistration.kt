package com.lyeeedar.Screens

import com.lyeeedar.Util.Statics

actual enum class ScreenEnum
{
	WORLD,
	PARTICLEEDITOR,
	MAPGENERATIONPREVIEW,
	ABILITYPREVIEW,
	INVALID
}

actual fun registerDebugScreens(): HashMap<ScreenEnum, AbstractScreen>
{
	val screens = HashMap<ScreenEnum, AbstractScreen>()

	if (!Statics.android)
	{
		screens[ScreenEnum.PARTICLEEDITOR] = ParticleEditorScreen()
		screens[ScreenEnum.MAPGENERATIONPREVIEW] = MapGenerationPreviewScreen()
		screens[ScreenEnum.ABILITYPREVIEW] = AbilityPreviewScreen()
	}

	return screens
}

actual fun registerGameScreens(): HashMap<ScreenEnum, AbstractScreen>
{
	val screens = HashMap<ScreenEnum, AbstractScreen>()

	screens[ScreenEnum.WORLD] = WorldScreen()

	return screens
}