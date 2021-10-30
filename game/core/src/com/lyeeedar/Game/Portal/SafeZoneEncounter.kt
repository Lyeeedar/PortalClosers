package com.lyeeedar.Game.Portal

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.lyeeedar.Screens.PortalScreen
import com.lyeeedar.UI.SpriteWidget
import com.lyeeedar.Util.AssetManager

class SafeZoneEncounter : AbstractEncounter()
{
	override val title: String
		get() = TODO("Not yet implemented")
	override val description: String
		get() = TODO("Not yet implemented")

	override fun createPreviewTable(): Table
	{
		TODO("Not yet implemented")
	}

	override fun actions(screen: PortalScreen): Sequence<Pair<String, () -> Unit>>
	{
		TODO("Not yet implemented")
	}

	override fun getMapWidget(): Widget
	{
		return SpriteWidget(AssetManager.loadSprite("GUI/ItemCardback"), 48f, 48f)
	}
}