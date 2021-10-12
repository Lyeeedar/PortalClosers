package com.lyeeedar.Screens

import com.lyeeedar.Util.Statics

actual enum class ScreenEnum
{
	WORLD,
	PARTICLEEDITOR,
	MAPGENERATIONPREVIEW,
	ABILITYPREVIEW,
	TEST,
	PORTAL,
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
		screens[ScreenEnum.TEST] = AnimationTestScreen()
	}

	return screens
}

actual fun registerGameScreens(): HashMap<ScreenEnum, AbstractScreen>
{
	val screens = HashMap<ScreenEnum, AbstractScreen>()

	screens[ScreenEnum.WORLD] = WorldScreen()
	screens[ScreenEnum.PORTAL] = PortalScreen()

	return screens
}