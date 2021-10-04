package com.lyeeedar.desktop

import com.lyeeedar.MainGame
import com.lyeeedar.Screens.ScreenEnum
import com.lyeeedar.Util.Statics

object AbilityPreviewLauncher
{
	@JvmStatic fun main(arg: Array<String>)
	{
		Statics.game = MainGame(ScreenEnum.ABILITYPREVIEW)
		Statics.applicationChanger = LwjglApplicationChanger()
		Statics.applicationChanger.createApplication()
	}
}