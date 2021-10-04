package com.lyeeedar.desktop

import com.lyeeedar.MainGame
import com.lyeeedar.Screens.ScreenEnum
import com.lyeeedar.Util.Statics

object MapGenerationPreviewLauncher
{
	@JvmStatic fun main(arg: Array<String>)
	{
		Statics.game = MainGame(ScreenEnum.MAPGENERATIONPREVIEW)
		Statics.applicationChanger = LwjglApplicationChanger()
		Statics.applicationChanger.createApplication()
	}
}