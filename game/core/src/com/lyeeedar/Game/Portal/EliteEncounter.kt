package com.lyeeedar.Game.Portal

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.lyeeedar.Components.EntityLoader
import com.lyeeedar.Components.renderable
import com.lyeeedar.Renderables.SkeletonRenderable
import com.lyeeedar.Screens.PortalScreen
import com.lyeeedar.UI.SkeletonWidget
import com.lyeeedar.UI.tint
import com.lyeeedar.Util.Colour

class EliteEncounter : AbstractEncounter()
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
		val entity = EntityLoader.load("Entities/elemental2")
		val skeleton = entity.renderable()!!.renderable as SkeletonRenderable
		skeleton.colour = Colour.ORANGE
		return SkeletonWidget(skeleton, 48f, 48f).tint(Color.ORANGE) as Widget
	}
}